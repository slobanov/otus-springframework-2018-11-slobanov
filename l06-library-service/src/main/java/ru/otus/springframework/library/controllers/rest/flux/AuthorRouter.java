package ru.otus.springframework.library.controllers.rest.flux;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.flux.AuthorServiceFlux;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.flux.BookServiceFlux;

import java.util.function.LongFunction;

import static java.lang.Long.valueOf;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Configuration
@Profile("flux")
class AuthorRouter {

    @Bean
    RouterFunction<ServerResponse> authorRouterFunction(AuthorHandler authorHandler) {
        return route()
            .GET("/api/v2/author", r -> authorHandler.all())
            .GET("/api/v2/author/{id}", authorHandler::getAuthor)
            .GET("/api/v2/author/{id}/books", authorHandler::writtenBy)
            .DELETE("/api/v2/author/{id}", authorHandler::removeAuthor)
            .POST("/api/v2/author", authorHandler::newAuthor)
            .build();
    }

}

@Component
@Profile("flux")
@RequiredArgsConstructor
class AuthorHandler {
    private final AuthorServiceFlux authorService;
    private final BookServiceFlux bookService;

    private static Mono<ServerResponse> authorIdOperation(
            LongFunction<Mono<?>> authorServiceOp,
            ServerRequest request
    ) {
        return authorServiceOp.apply(authorId(request))
                .flatMap(a -> ok().syncBody(a))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> newAuthor(ServerRequest request) {
        return request.formData().flatMap(params -> status(CREATED).body(
                authorService.newAuthor(params.getFirst("firstName"), params.getFirst("lastName")),
                Author.class
        ));
    }

    Mono<ServerResponse> all() {
        return ok().body(authorService.all(), Author.class);
    }

    Mono<ServerResponse> getAuthor(ServerRequest request) {
        return authorIdOperation(authorService::withId, request);
    }

    Mono<ServerResponse> removeAuthor(ServerRequest request) {
        return authorIdOperation(authorService::removeAuthor, request);
    }

    Mono<ServerResponse> writtenBy(ServerRequest request) {
        return ok().body(bookService.writtenBy(authorId(request)), Book.class);
    }

    private static Long authorId(ServerRequest serverRequest) {
        return valueOf(serverRequest.pathVariable("id"));
    }

}

