package com.ndl.luklak.data;

import com.ndl.idealer2.eventbus.server.EventBusServerConfig;
import com.ndl.idealer2.http.config.HttpServerConfig;
import lombok.Getter;

@Getter
public class AppConfig {
  private HttpServerConfig publicHttp;
  private EventBusServerConfig msgEventBus;
}
