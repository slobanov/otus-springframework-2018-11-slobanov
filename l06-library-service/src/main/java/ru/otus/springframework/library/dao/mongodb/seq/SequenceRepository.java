package ru.otus.springframework.library.dao.mongodb.seq;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class SequenceRepository {

    private final MongoOperations mongodbOps;

    public Long getNextSequence(String seqName) {
        Sequence counter = mongodbOps.findAndModify(
                query(where("_id").is(seqName)),
                new Update().inc("seq",1L),
                options().returnNew(true).upsert(true),
                Sequence.class
        );
        return counter.getSeq();
    }
}
