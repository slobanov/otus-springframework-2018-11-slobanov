package ru.otus.springframework.library.dao.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.authors.Author;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
public interface ReactiveAuthorMongodbRepository extends ReactiveBaseRepository<Author>, CustomSave<Author> {}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
class ReactiveAuthorMongodbRepositoryImpl implements CustomSave<Author> {

    private final ReactiveSequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private ReactiveAuthorMongodbRepository authorRepository;

    @Override
    public Mono<Author> saveObj(Author obj) {
        return sequenceRepository
                .getNextSequence("author")
                .flatMap(id -> {
                    obj.setId(id);
                    return authorRepository.save(obj);
                });
    }

}
