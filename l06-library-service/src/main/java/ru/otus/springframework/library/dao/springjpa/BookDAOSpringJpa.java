package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface BookDAOSpringJpa extends BaseDAOSpringJpa<Book>, BookDAO {

    String BOOK_SELECT =
            "         SELECT distinct b " +
            "           FROM Book b " +
            "     JOIN FETCH b.authors " +
            "     JOIN FETCH b.genres ";

    @Override
    @Query(BOOK_SELECT)
    List<Book> findAll();

    @Override
    @Query(BOOK_SELECT +  "WHERE b.isbn = :isbn")
    Optional<Book> findByIsbn(@Param("isbn") String isbn);

    @Override
    @Query(BOOK_SELECT + "WHERE :author MEMBER OF b.authors")
    List<Book> findByAuthors(@Param("author") Author author);

    @Override
    @Query(BOOK_SELECT + "WHERE :genre MEMBER OF b.genres")
    List<Book> findByGenres(@Param("genre") Genre genre);

    @Override
    default Book addAuthor(Book book, Author author) {
        book.getAuthors().add(author);
        save(book);
        return book;
    }

    @Override
    default Book addGenre(Book book, Genre genre) {
        book.getGenres().add(genre);
        save(book);
        return book;
    }
}
