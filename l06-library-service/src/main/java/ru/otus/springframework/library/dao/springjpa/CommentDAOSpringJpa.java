package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.CommentDAO;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface CommentDAOSpringJpa extends BaseDAOSpringJpa<Comment>, CommentDAO {

    @Override
    default List<Comment> findByBookId(Long bookId) {
        return findByBook_Id(bookId);
    }

    List<Comment> findByBook_Id(Long bookId);
}
