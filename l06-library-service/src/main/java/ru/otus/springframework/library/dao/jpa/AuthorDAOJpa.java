package ru.otus.springframework.library.dao.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
class AuthorDAOJpa extends SimpleDAOJpa<Author> implements AuthorDAO {

    AuthorDAOJpa() {
        super(Author.class);
    }

}
