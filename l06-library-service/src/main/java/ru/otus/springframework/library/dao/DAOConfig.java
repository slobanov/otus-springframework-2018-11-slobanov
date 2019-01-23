package ru.otus.springframework.library.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.BookBase;
import ru.otus.springframework.library.genres.Genre;

import static java.util.Map.of;

@Configuration
class DAOConfig {

    @Bean
    SimpleDAO<Author> authorDAO(NamedParameterJdbcOperations jdbcOperations) {
        return new SimpleDAOImpl<>(
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
        return new SimpleDAOImpl<>(
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
    SimpleDAO<BookBase> bookBaseDAO(NamedParameterJdbcOperations jdbcOperations) {
        return new SimpleDAOImpl<>(
                "BOOK",
                (r, i) -> new BookBase(
                        r.getLong("ID"),
                        r.getString("ISBN"),
                        r.getString("TITLE")
                ),
                of(
                        "ISBN", "isbn",
                        "TITLE", "title"

                ),
                jdbcOperations
        );
    }
}
