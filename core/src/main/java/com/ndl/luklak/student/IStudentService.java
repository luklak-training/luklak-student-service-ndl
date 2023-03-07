package com.ndl.luklak.student;

import com.ndl.luklak.common.response.Listing;

import java.util.concurrent.CompletionStage;

public interface IStudentService {
  CompletionStage<StudentResponse> create(Student student);

  CompletionStage<Listing> listing(int page, int pageSize);

  CompletionStage<Void> delete(int id);
}
