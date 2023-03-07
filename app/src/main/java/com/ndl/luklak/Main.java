package com.ndl.luklak;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.config.Config;
import com.ndl.common.preboot.YamlConfigReader;
import com.ndl.luklak.data.AppConfig;
import com.ndl.luklak.student.module.StudentModule;
import com.ndl.luklak.verticle.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class Main {

  public static void main(String[] args) {
    Config hazelcastConfig = new Config();
    ClusterManager mgr
      = new HazelcastClusterManager(hazelcastConfig);
    VertxOptions options = new VertxOptions()
      .setClusterManager(mgr);

    String containerAddress = getAddress();

    log.info("Container Address {}", containerAddress);

    EventBusOptions ebOptions = new EventBusOptions()
      .setHost(containerAddress);

    options.setEventBusOptions(ebOptions);

    AppConfig config = loadConfig();

    Injector injector = Guice.createInjector(new StudentModule(Vertx.vertx(), config));


    Vertx.clusteredVertx(options, handler -> {
      if (handler.succeeded()) {
        handler.result()
          .deployVerticle(new MainVerticle(injector, config),
            new DeploymentOptions(),
            deployHandler -> {
              if (deployHandler.succeeded()) {
                log.info
                  ("Verticle Deployed");
              } else {
                log.error("Verticle Deployment Failed: {}", deployHandler.cause().getMessage());
              }
            });
      }
    });
  }

  private static String getAddress() {
    try {
      List<NetworkInterface> networkInterfaces = new LinkedList<>();
      NetworkInterface.getNetworkInterfaces()
        .asIterator().forEachRemaining(networkInterfaces::add);
      return networkInterfaces.stream()
        .flatMap(iface -> iface.inetAddresses()
          .filter(entry -> entry.getAddress().length == 4)
          .filter(entry -> !entry.isLoopbackAddress())
          .filter(entry -> entry.getAddress()[0] !=
            Integer.valueOf(10).byteValue())
          .map(InetAddress::getHostAddress))
        .findFirst().orElse(null);
    } catch (SocketException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @SneakyThrows
  public static AppConfig loadConfig() {
    return YamlConfigReader.forType(AppConfig.class).readYaml(MainVerticle.class.getClassLoader().getResourceAsStream("appConfig.yaml"));
  }
}
