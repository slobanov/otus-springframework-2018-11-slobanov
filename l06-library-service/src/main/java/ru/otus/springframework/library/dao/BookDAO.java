package ru.otus.springframework.library.dao;

import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

public interface BookDAO extends SimpleDAO<Book> {
    List<Book> findByAuthors(Author author);
    List<Book> findByGenres(Genre genre);

    Optional<Book> findByIsbn(String isbn);

    Book addAuthor(Book book, Author author);
    Book addGenre(Book book, Genre genre);
}
