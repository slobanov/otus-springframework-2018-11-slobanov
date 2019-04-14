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
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.flux.BookServiceFlux;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.flux.GenreServiceFlux;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Configuration
@Profile("flux")
class GenreRouter {

    @Bean
    RouterFunction<ServerResponse> genreRouterFunction(GenreHandler genreHandler) {
        return route()
            .GET("/api/v2/genre", r -> genreHandler.all())
            .POST("/api/v2/genre", genreHandler::newGenre)
            .DELETE("/api/v2/genre/{genre}", genreHandler::removeGenre)
            .GET("/api/v2/genre/{genre}/books", genreHandler::ofGenre)
            .build();
    }

}

@Component
@Profile("flux")
@RequiredArgsConstructor
class GenreHandler {
    private final GenreServiceFlux genreService;
    private final BookServiceFlux bookService;

    Mono<ServerResponse> all() {
        return ok().body(genreService.all(), Genre.class);
    }

    Mono<ServerResponse> newGenre(ServerRequest request) {
        return request.formData().flatMap(
                params -> status(CREATED).body(genreService.newGenre(params.getFirst("genre")), Genre.class)
        );
    }

    Mono<ServerResponse> removeGenre(ServerRequest request) {
        return genreService.removeGenre(genre(request))
                .flatMap(a -> ok().syncBody(a))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> ofGenre(ServerRequest request) {
        return ok().body(bookService.ofGenre(genre(request)), Book.class);
    }

    private static String genre(ServerRequest request) {
        return request.pathVariable("genre");
    }
}

