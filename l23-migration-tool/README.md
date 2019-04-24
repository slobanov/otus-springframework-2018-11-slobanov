# l23-migration-tool

This is an utility for data (books, authors, genres and comments) migration 
from [PostgreSQL](https://www.postgresql.org/) to [MongoDB](https://www.mongodb.com/)
for [l06-library-service](../l06-library-service) application.
Internally, migration tool developed using [Spring Batch](https://spring.io/projects/spring-batch).
 
How to run:
 * To build app from sources execute `$ mvn clean package`. 
Then, run `$ java -jar target/l23-migration-tool.jar` to start the job.
 * Job can be started using `$ docker run --rm otusspring201811slobanov/l23-migration-tool`.
 
__NB__: By default, migration tool expects [MongoDB](https://www.mongodb.com/) at `localhost:27017`
and [PostgreSQL](https://www.postgresql.org/) at `localhost:5432`. 
It is possible to change these locations in `application.yml`.

- - - -

Sergey Lobanov
[s.y.lobanov@yandex.ru](mailto:s.y.lobanov@yandex.ru?Subject=otus-springframework-2018-11-slobanov)
