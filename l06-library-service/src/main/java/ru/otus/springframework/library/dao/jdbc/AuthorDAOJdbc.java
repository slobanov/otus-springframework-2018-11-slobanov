package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jdbc")
class AuthorDAOJdbc extends SimpleDAOJdbc<Author> implements AuthorDAO {

    private static final String TABLE_NAME = "AUTHOR";
    private static final String INSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + " (FIRST_NAME, LAST_NAME) VALUES (:firstName, :lastName)";

    private static final RowMapper<Author> AUTHOR_ROW_MAPPER = (r, i) -> new Author(
            r.getLong("ID"),
            r.getString("FIRST_NAME"),
            r.getString("LAST_NAME")
    );

    AuthorDAOJdbc(NamedParameterJdbcOperations jdbcOperations) {
        super(jdbcOperations, TABLE_NAME, INSERT_QUERY, AUTHOR_ROW_MAPPER);
    }

}
