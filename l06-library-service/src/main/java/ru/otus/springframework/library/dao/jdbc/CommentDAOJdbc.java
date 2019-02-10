package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.CommentDAO;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;

import static java.util.Map.of;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jdbc")
class CommentDAOJdbc extends SimpleDAOJdbc<Comment> implements CommentDAO {

    private static final String TABLE_NAME = "COMMENT";
    private static final String INSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + " (BOOK_ID, TEXT) VALUES (:bookId, :text)";

    CommentDAOJdbc(NamedParameterJdbcOperations jdbcOperations, SimpleDAO<Book> bookDAO) {
        super(jdbcOperations, TABLE_NAME, INSERT_QUERY,
                (r, i) -> new Comment(
                        r.getLong("ID"),
                        bookDAO.findById(r.getLong("BOOK_ID"))
                                .orElseThrow(() -> new IllegalStateException("Failed to retrieve book")),
                        r.getString("TEXT"),
                        r.getTimestamp("CREATED")
                )
        );
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        return getJdbcOperations().query(
                "SELECT * FROM " + TABLE_NAME + " WHERE BOOK_ID = :BOOK_ID",
                of("BOOK_ID", bookId),
                getRowMapper()
        );
    }

}
