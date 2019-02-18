package ru.otus.springframework.library.dao.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.Optional;

import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Repository
@Slf4j
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
public class GenreDAOJpa extends SimpleDAOJpa<Genre> implements GenreDAO {

    GenreDAOJpa() {
        super(Genre.class);
    }

    @Override
    public Optional<Genre> findByName(String name) {
        log.debug("find by name: {}", name);

        var genre = asSingle(getEm().createQuery(
                getSelectQuery() + " WHERE name = :NAME",
                Genre.class
        ).setParameter("NAME", name)
         .getResultList());

        log.debug("found by name [{}] : {}", name, genre);

        return genre;
    }
}
