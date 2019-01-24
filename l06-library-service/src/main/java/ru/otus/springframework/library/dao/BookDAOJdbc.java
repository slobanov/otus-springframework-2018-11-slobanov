package ru.otus.springframework.library.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookBase;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;

import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;
import static ru.otus.springframework.library.utils.OptionalUtils.flatten;

@Repository
@Slf4j
@RequiredArgsConstructor
class BookDAOJdbc implements BookDAO {

    private final NamedParameterJdbcOperations jdbcOperations;

    private final SimpleDAO<BookBase> bookBaseDAO;
    private final SimpleDAO<Author> authorDAO;
    private final SimpleDAO<Genre> genreDAO;

    @Override
    public List<Book> fetchAll() {
        var bookBases = bookBaseDAO.fetchAll();
        return StreamEx.of(bookBases).map(this::fromBookBase).toList();
    }

    private Book fromBookBase(BookBase bookBase) {
        var bookId = bookBase.getId();

        var authorIds = jdbcOperations.queryForList(
                "SELECT AUTHOR_ID FROM BOOK_TO_AUTHOR WHERE BOOK_ID = :BOOK_ID",
                of("BOOK_ID", bookId),
                Long.class
        );
        var genreIds = jdbcOperations.queryForList(
                "SELECT GENRE_ID FROM BOOK_TO_GENRE WHERE BOOK_ID = :BOOK_ID",
                of("BOOK_ID", bookId),
                Long.class
        );

        return new Book(
                bookId,
                bookBase.getIsbn(),
                bookBase.getTitle(),
                flatten(StreamEx.of(authorIds).map(authorDAO::findById)),
                flatten(StreamEx.of(genreIds).map(genreDAO::findById))
        );
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        var bookIds = jdbcOperations.queryForList(
                "SELECT BOOK_ID FROM BOOK_TO_AUTHOR WHERE AUTHOR_ID = :AUTHOR_ID",
                of("AUTHOR_ID", author.getId()),
                Long.class
        );
        return flatten(StreamEx.of(bookIds).map(this::findById));
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        var bookIds = jdbcOperations.queryForList(
                "SELECT BOOK_ID FROM BOOK_TO_GENRE WHERE GENRE_ID = :GENRE_ID",
                of("GENRE_ID", genre.getId()),
                Long.class
        );
        return flatten(StreamEx.of(bookIds).map(this::findById));
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookBaseDAO.findById(id).map(this::fromBookBase);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return asSingle(bookBaseDAO.findByField("ISBN", isbn)).map(this::fromBookBase);
    }

    @Override
    @Transactional
    public Book save(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var sqlProperties = new BeanPropertySqlParameterSource(book);

        jdbcOperations.update(
                "INSERT INTO BOOK (ISBN, TITLE) VALUES (:isbn, :title)",
                sqlProperties,
                keyHolder
        );

        var id = requireNonNull(keyHolder.getKey()).longValue();

        book.getAuthors().forEach(a -> jdbcOperations.update(
                "INSERT INTO BOOK_TO_AUTHOR (BOOK_ID, AUTHOR_ID) VALUES (:BOOK_ID, :AUTHOR_ID)",
                of(
                        "BOOK_ID", id,
                        "AUTHOR_ID", a.getId()
                )
        ));

        book.getGenres().forEach(g -> jdbcOperations.update(
                "INSERT INTO BOOK_TO_GENRE (BOOK_ID, GENRE_ID) VALUES (:BOOK_ID, :GENRE_ID)",
                of(
                        "BOOK_ID", id,
                        "GENRE_ID", g.getId()
                )
        ));

        return assureExists(id);
    }

    @Override
    public Optional<Book> deleteById(Long id) {
        var book = bookBaseDAO.findById(id).map(this::fromBookBase);
        bookBaseDAO.deleteById(id);
        return book;
    }

    @Override
    public Book addAuthor(Book book, Author author) {
        var id = book.getId();
        jdbcOperations.update(
                "INSERT INTO BOOK_TO_AUTHOR (BOOK_ID, AUTHOR_ID) VALUES (:BOOK_ID, :AUTHOR_ID)",
                of(
                        "BOOK_ID", id,
                        "AUTHOR_ID", author.getId()
                )
        );
        return assureExists(id);
    }

    @Override
    public Book addGenre(Book book, Genre genre) {
        var id = book.getId();
        jdbcOperations.update(
                "INSERT INTO BOOK_TO_GENRE (BOOK_ID, GENRE_ID) VALUES (:BOOK_ID, :GENRE_ID)",
                of(
                        "BOOK_ID", id,
                        "GENRE_ID", genre.getId()
                )
        );
        return assureExists(id);
    }

    private Book assureExists(Long bookId) {
        return findById(bookId).orElseThrow(
                () -> new IllegalStateException("Failed to retrieve updated book with id " + bookId)
        );
    }
}
