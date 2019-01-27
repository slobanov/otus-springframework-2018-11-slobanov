package ru.otus.springframework.library.dao.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.genres.Genre;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Repository
@Slf4j
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jpa")
class BookDAOJpa implements BookDAO {

    @PersistenceContext
    private EntityManager em;

    private static final String BOOK_SELECT =
       "         SELECT distinct b " +
       "           FROM Book b " +
       "     JOIN FETCH b.authors " +
       "     JOIN FETCH b.genres " +
       "LEFT JOIN FETCH b.comments";

    @Override
    public List<Book> fetchAll() {
        return em.createQuery(
                BOOK_SELECT,
                Book.class
        ).getResultList();
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        return em.createQuery(BOOK_SELECT +
                        " WHERE :author MEMBER OF b.authors",
                Book.class
        ).setParameter("author", author).getResultList();
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        return em.createQuery(BOOK_SELECT +
                        " WHERE :genre MEMBER OF b.genres",
                Book.class
        ).setParameter("genre", genre).getResultList();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return asSingle(em.createQuery(BOOK_SELECT +
                        " WHERE b.isbn = :isbn",
                Book.class
        ).setParameter("isbn", isbn).getResultList());
    }

    @Override
    @Transactional
    public Book save(Book book) {
        em.persist(book);
        return book;
    }

    @Override
    @Transactional
    public Optional<Book> deleteById(Long id) {
        var book = findById(id);
        book.ifPresent(em::remove);
        return book;
    }

    @Override
    @Transactional
    public Book addAuthor(Book book, Author author) {
        book.getAuthors().add(author);
        return em.merge(book);
    }

    @Override
    @Transactional
    public Book addGenre(Book book, Genre genre) {
        book.getGenres().add(genre);
        return em.merge(book);
    }

}
