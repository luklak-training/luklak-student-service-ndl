package com.ndl.luklak.student.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.ndl.idealer2.core.api.IRequest;
import com.ndl.idealer2.core.api.RegisterIApi;
import com.ndl.luklak.student.IStudentService;
import com.ndl.luklak.student.Student;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.CompletionStage;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
@RegisterIApi(method = "http.post", endpoint = "/api/students", tag = {"public"})
public class StoreStudentHandler extends DataJsonResponseApi {
  @NonNull
  private final IStudentService studentService;

  @SneakyThrows
  @Override
  protected CompletionStage<?> handleReturningData(IRequest request) {
    ObjectMapper mapper = new ObjectMapper();
    Student student = mapper.readValue(request.getBody().toString(), Student.class);
    return studentService.create(student);
  }
}
