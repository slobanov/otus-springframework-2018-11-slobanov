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
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.flux.CommentServiceFlux;

import java.util.function.Function;

import static java.lang.Long.valueOf;
import static java.util.Objects.requireNonNull;
import static one.util.streamex.StreamEx.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Configuration
@Profile("flux")
class BookRouter {

    @Bean
    RouterFunction<ServerResponse> bookRouterFunction(BookHandler bookHandler) {
        return route()
            .GET("/api/v2/book", r -> bookHandler.all())
            .GET("/api/v2/book/{isbn}", bookHandler::getBook)
            .DELETE("/api/v2/book/{isbn}", bookHandler::removeBook)
            .POST("/api/v2/book", bookHandler::newBook)
            .GET("/api/v2/book/{isbn}/comments", bookHandler::comments)
            .POST("/api/v2/book/{isbn}/comments", bookHandler::addComment)
            .POST("/api/v2/book/{isbn}/authors", bookHandler::addAuthor)
            .POST("/api/v2/book/{isbn}/genres", bookHandler::addGenre)
            .build();
    }

}

@Component
@Profile("flux")
@RequiredArgsConstructor
class BookHandler {

    private final BookServiceFlux bookService;
    private final CommentServiceFlux commentService;

    private static String isbn(ServerRequest serverRequest) {
        return serverRequest.pathVariable("isbn");
    }

    Mono<ServerResponse> all() {
        return ok().body(bookService.all(), Book.class);
    }

    Mono<ServerResponse> getBook(ServerRequest request) {
        return bookIsbnOperation(bookService::withIsbn, request);
    }

    Mono<ServerResponse> removeBook(ServerRequest request) {
        return bookIsbnOperation(bookService::removeBook, request);
    }

    private static Mono<ServerResponse> bookIsbnOperation(
            Function<String, Mono<Book>> bookServiceOp, ServerRequest request
    ) {
        return bookServiceOp.apply(isbn(request))
                .flatMap(book -> ok().syncBody(book))
                .switchIfEmpty(notFound().build());
    }

    Mono<ServerResponse> newBook(ServerRequest request) {
        return request.formData().flatMap(params -> status(CREATED).body(
                bookService.newBook(
                        params.getFirst("isbn"),
                        params.getFirst("title"),
                        of(params.get("authorIds")).map(Long::valueOf).toList(),
                        params.get("genres")
                ),
                Book.class
        ));
    }

    Mono<ServerResponse> comments(ServerRequest request) {
        return ok().body(commentService.commentsFor(isbn(request)), Comment.class);
    }

    Mono<ServerResponse> addComment(ServerRequest request) {
        return request.formData().flatMap(params -> commentService.newComment(
                        isbn(request),
                        params.getFirst("comment")
                )).flatMap(comment -> ok().syncBody(comment.getBook()));
    }

    Mono<ServerResponse> addGenre(ServerRequest request) {
        return request.formData().flatMap(params -> ok().body(
                bookService.addGenre(isbn(request), params.getFirst("genre")),
                Book.class
        ));
    }

    Mono<ServerResponse> addAuthor(ServerRequest request) {
        return request.formData().flatMap(params -> ok().body(
                bookService.addAuthor(isbn(request), valueOf(requireNonNull(params.getFirst("authorId")))),
                Book.class
        ));
    }
}
