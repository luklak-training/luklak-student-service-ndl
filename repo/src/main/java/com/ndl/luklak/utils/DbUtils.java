package com.ndl.luklak.utils;

import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import com.ndl.common.uri.ParsedUri;
import com.ndl.luklak.student.PostgresConfig;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.Properties;

public class DbUtils {

  private static final String HOST_CONFIG = "datasource.host";
  private static final String PORT_CONFIG = "datasource.port";
  private static final String DATABASE_CONFIG = "datasource.database";
  private static final String USERNAME_CONFIG = "datasource.username";
  private static final String PASSWORD_CONFIG = "datasource.password";

  private DbUtils() {

  }

  public static PgPool buildDbClient(Vertx vertx) {
    final Properties properties = ConfigUtils.getInstance().getProperties();

    final PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(Integer.parseInt(properties.getProperty(PORT_CONFIG)))
      .setHost(properties.getProperty(HOST_CONFIG))
      .setDatabase(properties.getProperty(DATABASE_CONFIG))
      .setUser(properties.getProperty(USERNAME_CONFIG))
      .setPassword(properties.getProperty(PASSWORD_CONFIG));

    final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    return PgPool.pool(vertx, connectOptions, poolOptions);
  }

  public static PgConnectionSupplier buildPgConnectionSupplier(Vertx vertx, PostgresConfig postgresConfig) {
    ParsedUri parsedUri =
      ParsedUri.builder().address(postgresConfig.getHost() + ":" + postgresConfig.getPort())
        .user(postgresConfig.getUsername())
        .password(postgresConfig.getPassword())
        .path(postgresConfig.getDatabase())
        .scheme("postgres-steady").build();

    return PgConnectionSupplier.from(parsedUri, vertx);
  }
}
