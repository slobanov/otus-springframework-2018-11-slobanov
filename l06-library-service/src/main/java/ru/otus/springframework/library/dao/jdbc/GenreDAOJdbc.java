package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.Optional;

import static java.util.Map.of;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jdbc")
class GenreDAOJdbc extends SimpleDAOJdbc<Genre> implements GenreDAO {

    private static final String TABLE_NAME = "GENRE";
    private static final String INSERT_QUERY =
            "INSERT INTO " + TABLE_NAME + " (NAME) VALUES (:name)";

    private static final RowMapper<Genre> GENRE_ROW_MAPPER = (r, i) -> new Genre(
            r.getLong("ID"),
            r.getString("NAME")
    );

    GenreDAOJdbc(NamedParameterJdbcOperations jdbcOperations) {
        super(jdbcOperations, TABLE_NAME, INSERT_QUERY, GENRE_ROW_MAPPER);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        return asSingle(getJdbcOperations().query(
                "SELECT * FROM " + TABLE_NAME + " WHERE NAME = :NAME",
                of("NAME", name),
                GENRE_ROW_MAPPER
        ));
    }

}
