create table students
(
  id   bigserial
    constraint students_pk
      primary key,
  name text,
  age  integer
);
