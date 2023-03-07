package com.ndl.luklak.repository;

import com.ndl.common.pgpool.supplier.PgConnectionSupplier;
import com.ndl.common.sql.SqlQuery;
import com.ndl.common.sql.SqlResult;
import com.ndl.common.sql.SqlSingleResult;
import com.ndl.common.sql.dsl.Insert;
import com.ndl.common.sql.dsl.Select;
import com.ndl.luklak.student.IStudentRepository;
import com.ndl.luklak.student.Student;
import com.ndl.luklak.utils.PgBaseRepository;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

@Slf4j
public class PgStudentRepository extends PgBaseRepository implements IStudentRepository {
  public PgStudentRepository(@NonNull PgConnectionSupplier pgConnectionSupplier) {
    super(pgConnectionSupplier);
  }

  private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM students";

  private static final String SQL_DELETE = "DELETE FROM students WHERE id = $1";


  public CompletionStage<Student> insert(Student student) {
    SqlQuery sqlQuery = Insert.into("students")
      .value("name", student.getName())
      .value("age", student.getAge())
      .returnValues("id", "name", "age")
      .toSqlQuery();

    return execute(sqlQuery)
      .thenCompose(SqlSingleResult::closeThenGetFirst)
      .thenApply(this::toStudent)
      .exceptionally(e -> {
        throw new RuntimeException();
      });
  }

  public CompletionStage<List<Student>> listing(int page,
                                                int pageSize) {
    SqlQuery sqlQuery = Select.on("students")
      .paging(page, pageSize)
      .toSQL();

    return execute(sqlQuery)
      .thenCompose(SqlResult::closeThenGet)
      .thenApply(this::toStudents)
      .exceptionally(e -> {
        throw new RuntimeException();
      });
  }


  public CompletionStage<Integer> count() {
    return execute(SqlQuery.of(SQL_COUNT))
      .thenCompose(SqlSingleResult::closeThenGetFirst)
      .thenApply(row -> row.getInteger("total"))
      .exceptionally(e -> {
        throw new RuntimeException();
      });
  }

  public CompletionStage<Void> delete(int id) {
    return execute(SqlQuery.of(SQL_DELETE).withArg(id))
      .thenCompose(SqlSingleResult::close)
      .exceptionally(e -> {
        throw new RuntimeException();
      });
  }

  @SneakyThrows
  private Student toStudent(Row row) {
    if (Objects.isNull(row)) {
      throw new Exception("row is null");
    }
    return new Student()
      .setId(row.getInteger("id"))
      .setName(row.getString("name"))
      .setAge(row.getInteger("age"));
  }

  @SneakyThrows
  private List<Student> toStudents(RowSet<Row> rows) {
    if (rows.size() == 0) {
      throw new Exception("rows is empty!");
    }

    List<Student> result = new ArrayList<>();

    for (Row row : rows) {
      result.add(toStudent(row));
    }
    return result;
  }

}
