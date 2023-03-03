package com.ndl.luklak.student.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import com.ndl.idealer2.http.config.HttpStatusErrorMapping;
import com.ndl.luklak.repository.PgStudentRepository;
import com.ndl.luklak.student.IStudentRepository;
import com.ndl.luklak.student.IStudentService;
import com.ndl.luklak.student.StudentService;
import com.ndl.luklak.student.handler.ListingStudentHandler;
import com.ndl.luklak.student.handler.StoreStudentHandler;
import io.vertx.core.Vertx;
import lombok.AllArgsConstructor;

import static com.ndl.luklak.utils.DbUtils.buildPgConnectionSupplier;

@AllArgsConstructor
public class StudentModule extends AbstractModule {
  private final Vertx vertx;

  @Provides
  @Singleton
  public IStudentRepository studentRepository(PgConnectionSupplier pgConnectionSupplier) {
    return new PgStudentRepository(pgConnectionSupplier);
  }

  @Provides
  @Singleton
  public Vertx vertx() {
    return vertx;
  }

  @Provides
  @Singleton
  public IStudentService studentService(IStudentRepository studentRepository) {
    return new StudentService(studentRepository);
  }

  @Provides
  @Singleton
  ListingStudentHandler listingStudentHandler(IStudentService studentService) {
    return new ListingStudentHandler(studentService);
  }

  @Provides
  @Singleton
  public StoreStudentHandler storeStudentHandler(IStudentService studentService) {
    return new StoreStudentHandler(studentService);
  }

  @Provides
  @Singleton
  public HttpStatusErrorMapping httpStatusErrorMapping() {
    return HttpStatusErrorMapping.scanAndCreate();
  }

  @Provides
  @Singleton
  public PgConnectionSupplier pgConnectionSupplier() {
    return buildPgConnectionSupplier(vertx);
  }
}
