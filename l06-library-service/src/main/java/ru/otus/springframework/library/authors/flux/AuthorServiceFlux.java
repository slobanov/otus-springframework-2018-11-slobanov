package ru.otus.springframework.library.authors.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.authors.Author;

public interface AuthorServiceFlux {
    Flux<Author> all();
    Mono<Author> withId(Long id);
    Mono<Author> newAuthor(String firstName, String lastName);
    Mono<Author> removeAuthor(Long id);
}
