package ru.otus.springframework.library.genres.flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.dao.reactive.ReactiveGenreMongodbRepository;
import ru.otus.springframework.library.genres.Genre;

@Component
@Profile("flux")
@RequiredArgsConstructor
@Slf4j
public class GenreServiceFluxImpl implements GenreServiceFlux {

    private final ReactiveGenreMongodbRepository genreRepository;

    @Override
    public Flux<Genre> all() {
        log.info("all");
        return genreRepository.findAll()
                .doOnNext(genre -> log.debug("all; genre {}", genre));
    }

    @Override
    public Mono<Genre> newGenre(String name) {
        log.info("new genre name {}", name);
        return byName(name)
                .flatMap(genre -> Mono.<Genre>error(
                        () -> new IllegalArgumentException("genre with name " + name + " already exists"))
                ).switchIfEmpty(genreRepository.saveObj(new Genre(name)))
                 .doOnNext(genre -> log.debug("new genre name {}; genre {}", name, genre));
    }

    @Override
    public Mono<Genre> removeGenre(String name) {
        log.info("remove by name {}", name);
        return byName(name)
                .map(Genre::getId)
                .flatMap(genreRepository::deleteByObjId)
                .doOnNext(genre -> log.debug("remove by name {}; genre {}", name, genre));
    }

    private Mono<Genre> byName(String name) {
        return genreRepository.findByName(name);
    }
}
