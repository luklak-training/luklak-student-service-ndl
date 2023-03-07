package com.ndl.luklak.student;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostgresConfig {
  private String host;
  private Integer port;
  private String database;
  private String username;
  private String password;
}
