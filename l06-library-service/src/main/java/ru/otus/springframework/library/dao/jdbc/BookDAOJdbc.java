package ru.otus.springframework.library.dao.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.*;

import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Repository
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "jdbc")
class BookDAOJdbc implements BookDAO {

    private final NamedParameterJdbcOperations jdbcOperations;

    private static final String JOINED_SQL_REQUEST =
               "SELECT BOOK.ID as BOOK_ID," +
               "       BOOK.ISBN," +
               "       BOOK.TITLE," +
               "       AUTHOR.ID as AUTHOR_ID," +
               "       AUTHOR.FIRST_NAME," +
               "       AUTHOR.LAST_NAME," +
               "       GENRE.ID as GENRE_ID," +
               "       GENRE.NAME" +
               "       FROM BOOK " +
               "       JOIN BOOK_TO_AUTHOR " +
               "         ON BOOK.ID = BOOK_TO_AUTHOR.BOOK_ID " +
               "       JOIN AUTHOR " +
               "         ON AUTHOR.ID = BOOK_TO_AUTHOR.AUTHOR_ID " +
               "       JOIN BOOK_TO_GENRE " +
               "         ON BOOK.ID = BOOK_TO_GENRE.BOOK_ID " +
               "       JOIN GENRE " +
               "         ON GENRE.ID = BOOK_TO_GENRE.GENRE_ID";

    private static final ResultSetExtractor<List<Book>> BOOK_EXTRACTOR = rs -> {

        var isbnMap = new HashMap<Long, String>();
        var titleMap = new HashMap<Long, String>();
        var bookAuthorIds = new HashMap<Long, Set<Long>>();
        var bookGenreIds = new HashMap<Long, Set<Long>>();

        var firstNameMap = new HashMap<Long, String>();
        var lastNameMap = new HashMap<Long, String>();

        var genres = new HashMap<Long, String>();

        while (rs.next()) {
            var bookId = rs.getLong("BOOK_ID");
            isbnMap.putIfAbsent(bookId, rs.getString("ISBN"));
            titleMap.putIfAbsent(bookId, rs.getString("TITLE"));
            bookAuthorIds.putIfAbsent(bookId, new HashSet<>());
            bookGenreIds.putIfAbsent(bookId, new HashSet<>());

            var authorId = rs.getLong("AUTHOR_ID");
            bookAuthorIds.get(bookId).add(authorId);
            firstNameMap.putIfAbsent(authorId, rs.getString("FIRST_NAME"));
            lastNameMap.putIfAbsent(authorId, rs.getString("LAST_NAME"));

            var genreId = rs.getLong("GENRE_ID");
            bookGenreIds.get(bookId).add(genreId);
            genres.putIfAbsent(genreId, rs.getString("NAME"));
        }

        return StreamEx.of(isbnMap.keySet())
                .map(bookId -> {
                    var authors = StreamEx.of(bookAuthorIds.get(bookId))
                            .map(aId -> new Author(aId, firstNameMap.get(aId), lastNameMap.get(aId)))
                            .toSet();

                    var genreObjs = StreamEx.of(bookGenreIds.get(bookId))
                            .map(gId -> new Genre(gId, genres.get(gId)))
                            .toSet();

                    return new Book(
                            bookId,
                            isbnMap.get(bookId),
                            titleMap.get(bookId),
                            authors,
                            genreObjs
                    );
                }).toList();
    };


    @Override
    public List<Book> fetchAll() {
        return jdbcOperations.query(JOINED_SQL_REQUEST, BOOK_EXTRACTOR);
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        return jdbcOperations.query(
                JOINED_SQL_REQUEST +
                        " WHERE BOOK.ID IN (" +
                        "   SELECT BOOK_TO_AUTHOR.BOOK_ID" +
                        "     FROM BOOK_TO_AUTHOR" +
                        "    WHERE AUTHOR_ID = :ID " +
                        ")",
                of("ID", author.getId()),
                BOOK_EXTRACTOR
        );
    }

    @Override
    public List<Book> findByGenre(Genre genre) {
        return jdbcOperations.query(
                JOINED_SQL_REQUEST +
                        " WHERE BOOK.ID IN (" +
                        "   SELECT BOOK_TO_GENRE.BOOK_ID" +
                        "     FROM BOOK_TO_GENRE" +
                        "    WHERE GENRE_ID = :ID " +
                        ")",
                of("ID", genre.getId()),
                BOOK_EXTRACTOR
        );
    }

    @Override
    public Optional<Book> findById(Long id) {
        return asSingle(jdbcOperations.query(
                JOINED_SQL_REQUEST + " WHERE BOOK.ID = :ID",
                of("ID", id),
                BOOK_EXTRACTOR
        ));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return asSingle(jdbcOperations.query(
                JOINED_SQL_REQUEST + " WHERE BOOK.ISBN = :ISBN",
                of("ISBN", isbn),
                BOOK_EXTRACTOR
        ));
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
    @Transactional
    public Optional<Book> deleteByObjId(Long id) {
        var book = findById(id);
        jdbcOperations.update(
                "DELETE FROM BOOK WHERE ID = :ID",
                of("ID", id)
        );
        return book;
    }

    @Override
    @Transactional
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
    @Transactional
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
