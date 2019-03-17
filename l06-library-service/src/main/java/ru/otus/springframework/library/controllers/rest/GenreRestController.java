package ru.otus.springframework.library.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;
import java.util.Optional;

@RestController
@Profile("rest")
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping("/api/v2/genres")
    public List<Genre> all() {
        return genreService.all();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v2/genre/add")
    public Genre addGenre(@RequestParam String genre) {
        return genreService.newGenre(genre);
    }

    @DeleteMapping("/api/v2/genre/{genre}")
    public Optional<Genre> delete(@PathVariable String genre) {
        return genreService.removeGenre(genre);
    }

    @GetMapping("/api/v2/genre/{genre}/books")
    public List<Book> booksOfGenre(@PathVariable String genre) {
        return bookService.ofGenre(genre);
    }

}
