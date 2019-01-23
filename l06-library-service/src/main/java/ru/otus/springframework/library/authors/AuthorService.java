package ru.otus.springframework.library.authors;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> all();
    Optional<Author> withId(Long id);
    Author newAuthor(String firstName, String lastName);
    Optional<Author> removeAuthor(Long id);
}
