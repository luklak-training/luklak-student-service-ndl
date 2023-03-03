package com.ndl.luklak.student.handler;

import com.google.inject.Inject;
import com.ndl.idealer2.core.api.IRequest;
import com.ndl.idealer2.core.api.RegisterIApi;
import com.ndl.luklak.common.ResponseEntity;
import com.ndl.luklak.common.constant.Constant;
import com.ndl.luklak.common.response.Listing;
import com.ndl.luklak.student.IStudentService;
import io.vertx.ext.web.RoutingContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletionStage;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
@RegisterIApi(method = "http.get", endpoint = "/api/students", tag = {"public"})
public class ListingStudentHandler extends DataJsonResponseApi {
  @NonNull
  private final IStudentService studentService;

  public CompletionStage<Listing> listing(RoutingContext rc) {
    final int page = Integer.parseInt(rc.queryParams().get(Constant.PAGE_PARAMETER));
    final int pageSize = Integer.parseInt(rc.queryParams().get(Constant.PAGE_SIZE_PARAMETER));

    return studentService.listing(page, pageSize)
      .whenComplete((item, err) -> {
        if (err != null) {
          ResponseEntity.buildFailResponse(rc, err);
        } else {
          ResponseEntity.buildSuccessResponse(rc, item);
        }
      });
  }

  @Override
  protected CompletionStage<?> handleReturningData(IRequest request) {
    final int page = Integer.parseInt(request.getParam((Constant.PAGE_PARAMETER)));
    final int pageSize = Integer.parseInt(request.getParam(Constant.PAGE_SIZE_PARAMETER));

    return studentService.listing(page, pageSize);
  }
}
