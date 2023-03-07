package luklak;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ndl.common.preboot.YamlConfigReader;
import com.ndl.luklak.data.AppConfig;
import com.ndl.luklak.student.PostgresConfig;
import com.ndl.luklak.student.module.StudentModule;
import com.ndl.luklak.verticle.MainVerticle;
import io.vertx.core.Vertx;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Testcontainers
@Slf4j
public class TestDemo {
  public static Injector rootInjector;
  private static PostgreSQLContainer<?> pgContainer;

  private static Vertx vertx;

  private static AppConfig appConfig;

  @BeforeAll
  @SneakyThrows
  public static void setup() {
    vertx = Vertx.vertx();
    pgContainer = new PostgreSQLContainer<>("postgres:14");
    pgContainer.withDatabaseName("student");
    pgContainer.withUsername("root");
    pgContainer.withPassword("root");
    pgContainer.start();
    initDb(pgContainer);

    try (var in = TestDemo.class.getClassLoader().getResourceAsStream("app.yaml")) {
      appConfig = YamlConfigReader.forType(AppConfig.class).readYaml(in);
      PostgresConfig postgresConfig = new PostgresConfig()
        .setHost(pgContainer.getHost())
        .setPort(pgContainer.getMappedPort(5432))
        .setDatabase("student")
        .setUsername("root")
        .setPassword("root");

      appConfig.setDatasource(postgresConfig);
      rootInjector = Guice.createInjector(new StudentModule(vertx, appConfig));
      vertx.deployVerticle(new MainVerticle(rootInjector, appConfig));
    }
  }

  @Test
  @SneakyThrows
  public void createTest() {
    given().baseUri("http://localhost:8888")
      .headers(
        Map.of()
      )
      .body("""
        {"name": "Thanh", "age": 10}
        """)
      .post("v1/api/students", Map.of())
      .then()
      .log().all()
      .statusCode(200);
  }

  private static void initDb(PostgreSQLContainer pgContainer) {
    try (var input = TestDemo.class.getClassLoader().getResourceAsStream("init-db.sql");
         Connection connection = DriverManager.getConnection(pgContainer.getJdbcUrl(), pgContainer.getUsername(),
           pgContainer.getPassword())) {
      assert input != null;
      String sql = new String(input.readAllBytes());
      connection.createStatement().execute(sql);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
