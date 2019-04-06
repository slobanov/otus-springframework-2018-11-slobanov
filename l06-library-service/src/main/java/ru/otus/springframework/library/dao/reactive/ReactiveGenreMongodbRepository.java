package ru.otus.springframework.library.dao.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.genres.Genre;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
public interface ReactiveGenreMongodbRepository extends ReactiveBaseRepository<Genre>, CustomSave<Genre> {

    Mono<Genre> findByName(String name);

}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
class ReactiveGenreMongodbRepositoryImpl implements CustomSave<Genre> {

    private final ReactiveSequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private ReactiveGenreMongodbRepository genreRepository;

    @Override
    public Mono<Genre> saveObj(Genre obj) {
        return sequenceRepository
                .getNextSequence("genre")
                .flatMap(id -> {
                    obj.setId(id);
                    return genreRepository.save(obj);
                });
    }

}
