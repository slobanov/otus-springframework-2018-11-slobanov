package ru.otus.springframework.library.authors.flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.reactive.ReactiveAuthorMongodbRepository;

@Component
@Profile("flux")
@RequiredArgsConstructor
@Slf4j
class AuthorServiceFluxImpl implements AuthorServiceFlux {

    private final ReactiveAuthorMongodbRepository authorRepository;

    @Override
    public Flux<Author> all() {
        log.info("find all");
        return authorRepository.findAll()
                .doOnNext(a -> log.debug("find all; author: {}", a));
    }

    @Override
    public Mono<Author> withId(Long id) {
        log.info("with id: {}", id);
        return authorRepository.findById(id)
                .doOnNext(a -> log.debug("with id: {}; author: {}", id, a));
    }

    @Override
    public Mono<Author> newAuthor(String firstName, String lastName) {
        log.info("new author: firstName: {}, lastName: {}", firstName, lastName);
        return authorRepository.saveObj(new Author(firstName, lastName))
                .doOnNext(a -> log.debug("new author [{}, {}]: {}", firstName, lastName, a));
    }

    @Override
    public Mono<Author> removeAuthor(Long id) {
        log.info("delete id: {}", id);
        return authorRepository.deleteByObjId(id)
                .doOnNext(a -> log.debug("delete id: {}; author {}", id, a));
    }
}
