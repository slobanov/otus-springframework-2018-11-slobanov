package ru.otus.springframework.library.dao.reactive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.mongodb.BookComments;
import ru.otus.springframework.library.dao.mongodb.MongoComment;

import java.util.Date;
import java.util.Set;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public interface ReactiveCommentMongodbRepository {
    Flux<Comment> findByBookId(Long bookId);
    Mono<Comment> saveObj(Comment comment);
}

@Repository
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
class ReactiveCommentMongodbRepositoryImpl implements ReactiveCommentMongodbRepository {

    private final ReactiveSequenceRepository sequenceRepository;
    private final ReactiveMongoOperations mongoOps;
    private final ReactiveBookMongodbRepository bookRepository;

    @Override
    public Flux<Comment> findByBookId(Long bookId) {
        log.debug("comments for bookId: {}", bookId);
        var query = query(where("_id").is(bookId));
        query.fields().include("comments");
        return mongoOps.find(query, BookComments.class, "book")
                .flatMapIterable(book -> (book.getComments() != null) ? book.getComments() : Set.of())
                .flatMap(comment ->  bookRepository.findById(bookId).map(comment::asComment));
    }

    @Override
    public Mono<Comment> saveObj(Comment comment) {
        return sequenceRepository.getNextSequence("comment")
                .map(id -> {
                    comment.setId(id);
                    comment.setCreated(new Date());
                    return comment;
                })
                .flatMap(commentObj -> mongoOps.updateFirst(
                        query(where("_id").is(commentObj.getBook().getId())),
                        new Update().push("comments", MongoComment.fromComment(commentObj)),
                        BookComments.class
                ).thenReturn(commentObj));
    }
}

