package ru.otus.springframework.library.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;

import java.util.List;
import java.util.Optional;

@RestController
@Profile("rest")
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping("/api/v2/author")
    public List<Author> all() {
        return authorService.all();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v2/author")
    public Author addAuthor(
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        return authorService.newAuthor(firstName, lastName);
    }

    @GetMapping("/api/v2/author/{id}")
    public Optional<Author> author(@PathVariable Long id, Model model) {
        return authorService.withId(id);
    }

    @DeleteMapping("/api/v2/author/{authorId}")
    public Optional<Author> delete(@PathVariable Long authorId) {
        return authorService.removeAuthor(authorId);
    }

    @GetMapping("/api/v2/author/{id}/books")
    public List<Book> authorBooks(@PathVariable Long id) {
        return bookService.writtenBy(id);
    }
}
