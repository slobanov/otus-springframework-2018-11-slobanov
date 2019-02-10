package ru.otus.springframework.library.dao.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.CommentDAO;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
@Slf4j
public class CommentDAOJpa extends SimpleDAOJpa<Comment> implements CommentDAO {

    CommentDAOJpa() {
        super(Comment.class);
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        log.debug("find by bookId: {}", bookId);

        var comments = getEm().createQuery(
                getSelectQuery() + " WHERE book.id = :BOOK_ID",
                Comment.class
        ).setParameter("BOOK_ID", bookId)
         .getResultList();

        log.debug("found by bookId {} : {}", bookId, comments);

        return comments;
    }
}
