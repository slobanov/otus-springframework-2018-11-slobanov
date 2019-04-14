package ru.otus.springframework.library.dao.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Mono;

@NoRepositoryBean
interface ReactiveBaseRepository<T> extends ReactiveMongoRepository<T, Long> {

    default Mono<T> deleteByObjId(Long id) {
        return findById(id).flatMap(obj -> deleteById(id).thenReturn(obj));
    }

}
