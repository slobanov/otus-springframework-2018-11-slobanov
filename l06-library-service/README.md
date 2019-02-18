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
 * Or, app can be started using `$ docker run --rm -it --net=host otusspring201811slobanov/l06-library-service`.

__NB__: By default, library app uses [MongoDB](https://www.mongodb.com/) for data storage 
and expects to find one at `localhost:27017`.
But it also supports [PostgreSQL](https://www.postgresql.org/) and [in-memory H2 DB](http://www.h2database.com); 
to enable it set `spring.profiles.active` property to `postgres`/`h2` and
`library.db.url`, `library.db.username` and `library.db.password` to database credentials.
For docker image there are corresponding ENV parameters - `DB`, `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.
<br>
Here is a toy examples:
```bash
$ docker run --name mongodb \
   -d mongo
$ docker run -it --rm --link=mongodb \
    -e DB=mongodb \
    -e DB_URL=mongodb://mongodb:27017 \
    otusspring201811slobanov/l06-library-service
```

```bash
$ docker run --name postgres_db \
    -e POSTGRES_PASSWORD=password123 \
    -d postgres
$ docker run -it --rm --link=postgres_db \
    -e DB=postgres \
    -e DB_USER=postgres \
    -e DB_PASSWORD=password123 \
    -e DB_URL=jdbc:postgresql://postgres_db:5432/postgres \
    otusspring201811slobanov/l06-library-service
```

```bash
$ docker run -it --rm \
    -e DB=h2 \
    otusspring201811slobanov/l06-library-service
```
Also, for `postgres` and `h2` it is possible to switch between DAO providers (using relaxed binding) - app use spring-data-jpa repositories,
* to use hand-written JPA via Hibernate set ENV parameter `LIBRARY_DAO_PROVIDER=jpa`,
* to use plain old JDBC set parameter ENV `LIBRARY_DAO_PROVIDER=jdbc`
- - - -

Sergey Lobanov
[s.y.lobanov@yandex.ru](mailto:s.y.lobanov@yandex.ru?Subject=otus-springframework-2018-11-slobanov)
