package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface GenreDAOSpringJpa extends BaseDAOSpringJpa<Genre> {

    @Override
    default Map<String, Function<String, List<Genre>>> fieldMapper() {
        return Map.of("NAME", this::findByName);
    }

    List<Genre> findByName(String name);
}
