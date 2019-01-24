package ru.otus.springframework.library.dao;

import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

public interface BookDAO {
    List<Book> fetchAll();
    List<Book> findByAuthor(Author author);
    List<Book> findByGenre(Genre genre);

    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);

    Book save(Book book);
    Optional<Book> deleteById(Long id);

    Book addAuthor(Book book, Author author);
    Book addGenre(Book book, Genre genre);
}