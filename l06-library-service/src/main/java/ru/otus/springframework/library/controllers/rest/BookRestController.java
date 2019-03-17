package ru.otus.springframework.library.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.controllers.mvc.NotFoundException;

import java.util.List;
import java.util.Optional;

@RestController
@Profile("rest")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;
    private final CommentService commentService;

    @GetMapping("/api/v2/books")
    public List<Book> all() {
        return bookService.all();
    }

    @GetMapping("/api/v2/book/{isbn}")
    public Optional<Book> book(@PathVariable String isbn) {
        return bookService.withIsbn(isbn);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v2/book/add")
    public Book addBook(
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam List<Long> authorIds,
            @RequestParam List<String> genres
    ) {
        return bookService.newBook(isbn, title, authorIds, genres);
    }

    @DeleteMapping("/api/v2/book/{isbn}")
    public Optional<Book> delete(@PathVariable String isbn) {
        return bookService.removeBook(isbn);
    }

    @PostMapping("/api/v2/book/{isbn}/addAuthor")
    public Book addAuthor(@PathVariable String isbn, @RequestParam Long authorId) {
        return bookService.addAuthor(isbn, authorId);
    }

    @PostMapping("/api/v2/book/{isbn}/addGenre")
    public Book addGenre(@PathVariable String isbn, @RequestParam String genre) {
        return bookService.addGenre(isbn, genre);
    }

    @PostMapping("/api/v2/book/{isbn}/addComment")
    public Book addComment(@PathVariable String isbn, @RequestParam String comment) {
        commentService.newComment(isbn, comment);
        return bookService.withIsbn(isbn).orElseThrow(NotFoundException::new);
    }

}
