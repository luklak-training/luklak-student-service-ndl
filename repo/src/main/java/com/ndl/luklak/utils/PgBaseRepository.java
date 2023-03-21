package com.ndl.luklak.utils;

import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import com.ndl.common.sql.AbstractSqlClient;
import com.ndl.common.sql.SqlQueryMeta;
import io.vertx.sqlclient.SqlConnection;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.concurrent.CompletionStage;

@AllArgsConstructor
public class PgBaseRepository extends AbstractSqlClient {
  @NonNull
  protected final PgConnectionSupplier pgConnectionSupplier;

  @Override
  protected CompletionStage<? extends SqlConnection> getConnection(SqlQueryMeta sqlQueryMeta) {
    return pgConnectionSupplier.getConnection(sqlQueryMeta);
  }
}
