package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.comments.Comment;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface CommentDAOSpringJpa extends BaseDAOSpringJpa<Comment> {

    default Map<String, Function<String, List<Comment>>> fieldMapper() {
        return Map.of("BOOK_ID", s -> findByBook_Id(Long.parseLong(s)));
    }

    List<Comment> findByBook_Id(Long bookId);
}
