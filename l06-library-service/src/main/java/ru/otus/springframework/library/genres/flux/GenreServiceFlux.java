package ru.otus.springframework.library.genres.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.genres.Genre;

public interface GenreServiceFlux {
    Flux<Genre> all();
    Mono<Genre> newGenre(String name);
    Mono<Genre> removeGenre(String name);
}
