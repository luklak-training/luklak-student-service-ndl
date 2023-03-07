package com.ndl.luklak.data;

import com.ndl.idealer2.eventbus.server.EventBusServerConfig;
import com.ndl.idealer2.http.config.HttpServerConfig;
import com.ndl.luklak.student.PostgresConfig;
import lombok.Data;

@Data
public class AppConfig {
  private HttpServerConfig publicHttp;
  private EventBusServerConfig msgEventBus;
  private PostgresConfig datasource;
}
