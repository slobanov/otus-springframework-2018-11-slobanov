package ru.otus.springframework.library.dao.mongodb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.CommentDAO;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.function.Function.identity;
import static one.util.streamex.StreamEx.of;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
class CommentDAOMongodb implements CommentDAO {
    private final SequenceRepository sequenceRepository;
    private final MongoOperations mongoOps;
    private final BookDAO bookDAO;

    @Override
    public List<Comment> findByBookId(Long bookId) {
        log.debug("comments for bookId: {}", bookId);
        var book = bookDAO.findById(bookId);
        var query = query(where("_id").is(bookId));
        query.fields().include("comments");
        return of(mongoOps.find(query, BookComments.class, "book"))
                .flatCollection(BookComments::getComments)
                .flatMap(comment -> book.map(comment::asComment).stream())
                .toList();
    }

    @Override
    public List<Comment> findAll() {
       var bookCommentsMap = of(mongoOps.findAll(BookComments.class, "book"))
               .mapToEntry(BookComments::getId, identity())
               .toMap();

       return of(mongoOps.findAll(Book.class, "book"))
               .flatCollection(bk -> of(bookCommentsMap.get(bk.getId()).getComments())
                                      .map(cm -> cm.asComment(bk))
                                      .toList()
               ).toList();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        log.debug("findById: {}", id);
        var query = query(where("comments").elemMatch(where("id").is(id)));
        query.fields().elemMatch("comments", where("id").is(id)).include("id");
        var bookComments = Optional.ofNullable(mongoOps.findOne(
                query,
                BookComments.class
        ));
        var comment = bookComments
                .map(BookComments::getComments)
                .map(cs -> cs.iterator().next());
        var book = bookComments.flatMap(bc -> bookDAO.findById(bc.getId()));
        log.debug("bookComments: {}", bookComments);
        return book.flatMap(bk -> comment.map(cm -> cm.asComment(bk)));
    }

    @Override
    public Optional<Comment> deleteByObjId(Long id) {
        var comment = findById(id);
        comment.ifPresent(cm -> mongoOps.updateFirst(
                query(where("comments").elemMatch(where("id").is(id))),
                new Update().pull("comments", MongoComment.fromComment(cm)),
                BookComments.class
        ));
        return comment;
    }

    @Override
    public Comment saveObj(Comment comment) {
        comment.setId(sequenceRepository.getNextSequence("comment"));
        comment.setCreated(new Date());
        mongoOps.updateFirst(
                query(where("_id").is(comment.getBook().getId())),
                new Update().push("comments", MongoComment.fromComment(comment)),
                BookComments.class
        );
        return comment;
    }
}

