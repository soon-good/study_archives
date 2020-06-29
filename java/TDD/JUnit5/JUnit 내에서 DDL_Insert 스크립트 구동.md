# JUnit 내에서 DDL/Insert 스크립트 구동

데이터베이스에 데이터를 insert하는 방식은 

- @Sql
  - spring-jdbc
- @DataSet
  - spring-test-dbunit  

이 있다.  

여기서는 @Sql 을 활용한 DDL/Data Insert 스크립트 구동 방식을 정리한다.

