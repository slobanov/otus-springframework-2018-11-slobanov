package ru.otus.springframework.library.comments.flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.reactive.ReactiveBookMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveCommentMongodbRepository;

@Component
@Profile("flux")
@RequiredArgsConstructor
@Slf4j
class CommentServiceFluxImpl implements CommentServiceFlux {

    private final ReactiveBookMongodbRepository bookRepository;
    private final ReactiveCommentMongodbRepository commentRepository;

    @Override
    public Flux<Comment> commentsFor(String isbn) {
        log.info("comments for isbn: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .flatMapMany(book -> commentRepository.findByBookId(book.getId()))
                .doOnNext(comment -> log.debug("comment for isbn {}: {}", isbn, comment));
    }

    @Override
    public Mono<Comment> newComment(String isbn, String text) {
        log.info("new comment for book with isbn [{}]: {}", isbn, text);
        return bookRepository.findByIsbn(isbn)
                .flatMap(book -> commentRepository.saveObj(new Comment(book, text)))
                .switchIfEmpty(Mono.error(
                        () -> new IllegalArgumentException("There is no book with isbn = " + isbn))
                );
    }
}
