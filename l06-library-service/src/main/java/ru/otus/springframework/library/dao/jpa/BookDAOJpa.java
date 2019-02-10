package ru.otus.springframework.library.dao.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Repository
@Slf4j
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
class BookDAOJpa extends SimpleDAOJpa<Book> implements BookDAO {

    private static final String BOOK_SELECT =
       "         SELECT distinct b " +
       "           FROM Book b " +
       "     JOIN FETCH b.authors " +
       "     JOIN FETCH b.genres ";

    BookDAOJpa() {
        super(Book.class, BOOK_SELECT);
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        return getEm().createQuery(BOOK_SELECT +
                        " WHERE :author MEMBER OF b.authors",
                Book.class
        ).setParameter("author", author).getResultList();
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        return getEm().createQuery(BOOK_SELECT +
                        " WHERE :genre MEMBER OF b.genres",
                Book.class
        ).setParameter("genre", genre).getResultList();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return asSingle(getEm().createQuery(BOOK_SELECT +
                        " WHERE b.isbn = :isbn",
                Book.class
        ).setParameter("isbn", isbn).getResultList());
    }

    @Override
    @Transactional
    public Book addAuthor(Book book, Author author) {
        book.getAuthors().add(author);
        return getEm().merge(book);
    }

    @Override
    @Transactional
    public Book addGenre(Book book, Genre genre) {
        book.getGenres().add(genre);
        return getEm().merge(book);
    }

}
