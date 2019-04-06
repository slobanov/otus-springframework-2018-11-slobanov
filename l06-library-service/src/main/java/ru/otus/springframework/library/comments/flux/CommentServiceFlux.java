package ru.otus.springframework.library.comments.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.comments.Comment;

public interface CommentServiceFlux {
    Flux<Comment> commentsFor(String isbn);
    Mono<Comment> newComment(String isbn, String text);
}
