package ru.otus.springframework.library.dao.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.dao.mongodb.seq.Sequence;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
class ReactiveSequenceRepository {

    private final ReactiveMongoOperations mongodbOps;

    Mono<Long> getNextSequence(String seqName) {
        return mongodbOps.findAndModify(
                query(where("_id").is(seqName)),
                new Update().inc("seq",1L),
                options().returnNew(true).upsert(true),
                Sequence.class
        ).map(Sequence::getSeq);
    }
}
