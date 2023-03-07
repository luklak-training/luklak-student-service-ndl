package com.ndl.luklak.student.api;

import com.ndl.idealer2.core.api.IApi;
import com.ndl.idealer2.core.api.IRequest;
import com.ndl.idealer2.message.resp.NdlResponse;
import com.ndl.idealer2.message.resp.ProvidedBodyResponse;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.CompletionStage;

public abstract class DataJsonResponseApi implements IApi {

  private static final Buffer ACK = new JsonObject()
    .put("data", new JsonObject().put("message", "SUCCESS"))
    .toBuffer();

  @Override
  public final CompletionStage<NdlResponse<Buffer>> handle(IRequest request) throws Exception {
    return handleReturningData(request) //
      .thenApply(this::wrapResponse);
  }

  protected final Buffer ack(Object... any) {
    return ACK;
  }

  protected abstract CompletionStage<?> handleReturningData(IRequest request);

  private NdlResponse<Buffer> wrapResponse(Object body) {
    if (body instanceof Buffer buffer) {
      return new ProvidedBodyResponse<>(buffer);
    }
    if (body instanceof byte[] bytes) {
      return new ProvidedBodyResponse<>(Buffer.buffer(bytes));
    }
    return new ProvidedBodyResponse<>(new JsonObject()
      .put("data", body)
      .put("error", null))
      .transform(JsonObject::toBuffer);
  }
}
