package ru.otus.springframework.library.books;

import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> all();
    List<Book> writtenBy(Long authorId);
    List<Book> ofGenre(String genre);

    Optional<Book> withIsbn(String isbn);

    Book newBook(String isbn, String title, List<Long> authorsIds, List<String> genres);
    Optional<Book> removeBook(String isbn);

    Book addAuthor(String isbn, Long authorId);
    Book addGenre(String isbn, String genre);

    List<Genre> genresExceptBook(Book book);
    List<Author> authorsExceptBook(Book book);
}
