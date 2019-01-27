package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.SimpleDAO;
import ru.otus.springframework.library.genres.Genre;

import static java.util.Map.of;

@Configuration
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jdbc")
class JdbcDAOConfig {

    @Bean
    SimpleDAO<Author> authorDAO(NamedParameterJdbcOperations jdbcOperations) {
        return new SimpleDAOJdbc<>(
                "AUTHOR",
                (r, i) -> new Author(
                        r.getLong("ID"),
                        r.getString("FIRST_NAME"),
                        r.getString("LAST_NAME")
                ),
                of(
                        "FIRST_NAME", "firstName",
                        "LAST_NAME", "lastName"

                ),
                jdbcOperations
        );
    }

    @Bean
    SimpleDAO<Genre> genreDAO(NamedParameterJdbcOperations jdbcOperations) {
        return new SimpleDAOJdbc<>(
                "GENRE",
                (r, i) -> new Genre(
                        r.getLong("ID"),
                        r.getString("NAME")
                ),
                of("NAME", "name"),
                jdbcOperations
        );
    }


    @Bean
    SimpleDAO<Comment> commentDAO(NamedParameterJdbcOperations jdbcOperations) {
        return new SimpleDAOJdbc<>(
                "COMMENT",
                (r, i) -> new Comment(
                        r.getLong("ID"),
                        r.getLong("BOOK_ID"),
                        r.getString("TEXT"),
                        r.getTimestamp("CREATED")
                ),
                of(
                        "TEXT", "text",
                        "BOOK_ID", "bookId"
                ),
                jdbcOperations
        );
    }

}
