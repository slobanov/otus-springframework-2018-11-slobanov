# l06-library-service

This is a simple library application. It manages information about authors, books, genres and comments:
 * show all books, books written by author or books of particular genre.
 * modify books by adding authors and genres.
 * show all authors and books, add new ones; authors can be queried by id.
 * show all and add comments for book.
 * delete books, authors and genres.
 
All additional information about library usage can be seen after typing `help` in library menu.

Also, application uses [Spring Shell](https://projects.spring.io/spring-shell/) interface.

How to run:
 * To build app from sources execute `$ mvn clean package`. 
Then, run `$ java -jar target/l06-library-service.jar` to start the app.
 * Or, app can be started using `$ docker run --rm -it otusspring201811slobanov/l06-library-service`.

__NB__: By default, library app uses [in-memory H2 DB](http://www.h2database.com) for data storage.
But it also supports [PostgreSQL](https://www.postgresql.org/); 
to enable it set `spring.profiles.active` property to `postgres` and
`library.postgres.url`, `library.postgres.username` and `library.postgres.password` to database credentials.
For docker image there are corresponding ENV parameters - `DB`, `PG_URL`, `PG_USERNAME` and `PG_PASSWORD`.
<br>
Here is a toy example:
```bash
$ docker run --name postgres_db \
    -e POSTGRES_PASSWORD=password123 \
    -d postgres
$ docker run -it --rm --link=postgres_db \
    -e DB=postgres \
    -e PG_PASSWORD=password123 \
    -e PG_URL=jdbc:postgresql://postgres_db:5432/postgres \
    otusspring201811slobanov/l06-library-service
```
Also, it is possible to switch between DAO providers - app use spring-data-jpa repositories,
* to use hand-written JPA via Hibernate set parameter `library.dao.provider=jpa`,
* to use plain old JDBC set parameter `library.dao.provider=jdbc`

For docker image there is corresponding ENV `DAO_PROVIDER` parameter.

- - - -

Sergey Lobanov
[s.y.lobanov@yandex.ru](mailto:s.y.lobanov@yandex.ru?Subject=otus-springframework-2018-11-slobanov)
