# l06-library-service

This is a simple library application. It manages information about authors, books, genres and comments:
 * show all books, books written by author or books of particular genre.
 * modify books by adding authors and genres.
 * show all authors and books, add new ones; authors can be queried by id.
 * show all and add comments for book.
 * delete books, authors and genres.
 
How to run:
 * To build app from sources execute `$ mvn clean package`. 
Then, run `$ java -jar target/l06-library-service.jar` to start the app.
 * App can be started using `$ docker run --rm -it -p 8080:8080 otusspring201811slobanov/l06-library-service`.
 * Or, `docker-compose up -d` - there is ready-to-go [docker-compose.yml](docker-compose.yml).

__NB__: By default, library app runs on 8080 port and uses [MongoDB](https://www.mongodb.com/) for data storage 
and expects to find one at `localhost:27017`. For default `rest` profile there is REST API with [Swagger](https://swagger.io/) assets
(which means /swagger-ui.html and /v2/api-docs are available).

Also, for `rest` profile there is a feature of book delivery.
It does nothing (except adding the comment to the book) and exists for
[Spring Integration](https://spring.io/projects/spring-integration) application.

All library endpoints require authentication (if using `rest` or `mvc` with `mongodb`). There are some predefined users: 

| login       | password    | authorities          |
|-------------|-------------|----------------------|
| user        | password123 | all endpoints        |
| book_user   | book        | all book endpoints   | 
| author_user | author      | all author endpoints |
| genre_user  | genre       | all genre endpoints  |

Plus, these are accessible for every authenticated user: <br>
`GET /book` <br> `GET /api/v2/book` <br> `GET /author` <br> `GET /api/v2/author` <br> `GET /genre` <br> `GET /api/v2/genre`

For data storage app also supports [PostgreSQL](https://www.postgresql.org/) and [in-memory H2 DB](http://www.h2database.com); 
to enable it replace `mongodb` in `spring.profiles.active` property to `postgres`/`h2` and
`library.db.url`, `library.db.username` and `library.db.password` to database credentials.
For docker image there are corresponding ENV parameters - `DB`, `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`.
<br>
Here is a toy examples:
```bash
$ docker run --name mongodb \
   -d mongo
$ docker run -it --rm --link=mongodb \
    -p 8080:8080 \
    -e DB_URL=mongodb://mongodb:27017 \
    otusspring201811slobanov/l06-library-service
```

```bash
$ docker run --name postgres_db \
    -e POSTGRES_PASSWORD=password123 \
    -d postgres
$ docker run -it --rm --link=postgres_db \
    -p 8080:8080 \
    -e DB=postgres \
    -e DB_USERNAME=postgres \
    -e DB_PASSWORD=password123 \
    -e DB_URL=jdbc:postgresql://postgres_db:5432/postgres \
    otusspring201811slobanov/l06-library-service
```

```bash
$ docker run -it --rm \
    -p 8080:8080 \
    -e DB=h2 \
    otusspring201811slobanov/l06-library-service
```
For `postgres` and `h2` it is possible to switch between DAO providers (using relaxed binding) - app use spring-data-jpa repositories,
* to use hand-written JPA via Hibernate set ENV parameter `LIBRARY_DAO_PROVIDER=jpa`,
* to use plain old JDBC set parameter ENV `LIBRARY_DAO_PROVIDER=jdbc`

REST API can by disabled by changing profile from to `mvc` - 
this way web content will be generated using only [Thymeleaf](https://www.thymeleaf.org/).
As usual, there is corresponding ENV `UI` in docker image:
```bash
$ docker run -it --rm --link=mongodb \
    -e UI=mvc \
    -e DB_URL=mongodb://mongodb:27017 \
    otusspring201811slobanov/l06-library-service
```

Also, application can be started with [Spring Shell](https://projects.spring.io/spring-shell/) interface by switching profile to `shell`:
```bash
$ docker run -it --rm --link=mongodb \
    -e UI=shell \
    -e DB_URL=mongodb://mongodb:27017 \
    otusspring201811slobanov/l06-library-service
```
Servlet based REST API is replaced with reactive based on `flux` profile using [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html)
(this profile only compatible with `mongodb` profile and has no no api-docs available).
```bash
$ docker run -it --rm --link=mongodb \
    -e UI=flux \
    -e DB_URL=mongodb://mongodb:27017 \
    otusspring201811slobanov/l06-library-service
```

- - - -

Sergey Lobanov
[s.y.lobanov@yandex.ru](mailto:s.y.lobanov@yandex.ru?Subject=otus-springframework-2018-11-slobanov)
