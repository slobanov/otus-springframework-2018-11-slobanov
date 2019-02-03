package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface AuthorDAOSpringJpa extends BaseDAOSpringJpa<Author> {

    @Override
    default Map<String, Function<String, List<Author>>> fieldMapper() {
        return Map.of(
                "FIRST_NAME", this::findByFirstName,
                "LAST_NAME", this::findByLastName
        );
    }

    List<Author> findByFirstName(String firstName);
    List<Author> findByLastName(String lastName);
}
