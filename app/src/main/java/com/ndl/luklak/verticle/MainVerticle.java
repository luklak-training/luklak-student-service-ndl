package com.ndl.luklak.verticle;

import com.google.inject.Guice;
import com.ndl.common.preboot.YamlConfigReader;
import com.ndl.idealer2.core.registry.IApiRegistry;
import com.ndl.idealer2.eventbus.EventBusConsts;
import com.ndl.idealer2.eventbus.server.EndpointExtractor;
import com.ndl.idealer2.eventbus.server.EventBusServerConfig;
import com.ndl.idealer2.eventbus.server.IDealer2EventBusServer;
import com.ndl.idealer2.eventbus.server.ParamExtractorProvider;
import com.ndl.idealer2.http.IDealer2HttpServer;
import com.ndl.idealer2.http.config.HttpServerConfig;
import com.ndl.idealer2.http.config.HttpStatusErrorMapping;
import com.ndl.luklak.data.AppConfig;
import com.ndl.luklak.student.handler.DataJsonResponseApi;
import com.ndl.luklak.student.module.StudentModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var guice = Guice.createInjector(
      new StudentModule(vertx)
    );


    IApiRegistry apiRegistry = IApiRegistry.scan(guice, DataJsonResponseApi.class.getPackageName());
    AppConfig config = loadConfig();
    HttpStatusErrorMapping httpStatusErrorMapping = guice.getInstance(HttpStatusErrorMapping.class);


    deployHttpServer(apiRegistry, config.getPublicHttp(), httpStatusErrorMapping);
    deployEventBusConsumer(config.getMsgEventBus(), apiRegistry);

  }

  private CompletableFuture<String> deployHttpServer(IApiRegistry apiRegistry,
                                                     HttpServerConfig config,
                                                     HttpStatusErrorMapping errorMapping) {
    var httpServer = IDealer2HttpServer.builder()
      .apiRegistry(apiRegistry)
      .config(config)
      .errorMapping(errorMapping)
      .build();
    return vertx.deployVerticle(httpServer)
      .toCompletionStage()
      .toCompletableFuture();
  }

  private CompletableFuture<String> deployEventBusConsumer(EventBusServerConfig eventbusConfig, IApiRegistry apiRegistry) {
    var paramExtractor = ParamExtractorProvider.prefixed(EventBusConsts.PARAM_PREFIX);
    EndpointExtractor endpointExtractor = message -> message.headers().get("endpoint");
    var eventBus = IDealer2EventBusServer.builder()
      .paramExtractorProvider(paramExtractor)
      .endpointExtractor(endpointExtractor)
      .config(eventbusConfig)
      .apiRegistry(apiRegistry)
      .build();
    return vertx.deployVerticle(eventBus)
      .toCompletionStage()
      .toCompletableFuture();
  }

  @SneakyThrows
  public static AppConfig loadConfig() {
    return YamlConfigReader.forType(AppConfig.class).readYaml("app/src/main/resources/appConfig.yaml");
  }
}
