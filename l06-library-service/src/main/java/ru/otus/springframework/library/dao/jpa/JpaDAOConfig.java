package ru.otus.springframework.library.dao.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.SimpleDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.Map;

@Configuration
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
class JpaDAOConfig {

    @Bean
    SimpleDAO<Genre> genreDAO() {
        return new SimpleDAOJpa<>(
                Genre.class,
                s -> Map.of("NAME", "name").getOrDefault(s, s)
        );
    }

    @Bean
    SimpleDAO<Author> authorDAO() {
        return new SimpleDAOJpa<>(
                Author.class,
                s -> Map.of(
                        "FIRST_NAME", "firstName",
                        "LAST_NAME", "lastName"
                ).getOrDefault(s, s)
        );
    }

    @Bean
    SimpleDAO<Comment> commentDAO() {
        return new SimpleDAOJpa<>(
                Comment.class,
                s -> Map.of("TEXT", "text").getOrDefault(s, s)
        );
    }

}

