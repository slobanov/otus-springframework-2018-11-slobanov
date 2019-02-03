package ru.otus.springframework.library.dao;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.genres.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.params.provider.Arguments.of;

@Transactional
public abstract class BookDaoBaseTest {

    @Autowired
    private BookDAO bookDAOJdbc;

    @Autowired
    private SimpleDAO<Author> authorDAO;

    @Autowired
    private SimpleDAO<Genre> genreDAO;

    @Test
    void fetchAll() {
        var books = bookDAOJdbc.fetchAll();
        assertThat(books, hasSize(3));
        assertThat(StreamEx.of(books).map(Book::getIsbn).toList(),
                containsInAnyOrder("1", "2", "3")
        );
    }

    @ParameterizedTest
    @MethodSource("bookByAuthorProvider")
    void findByAuthor(Author author, Collection<Book> expected) {
        var books = new HashSet<>(bookDAOJdbc.findByAuthor(author));
        assertThat(books, equalTo(expected));
    }

    private static Stream<Arguments> bookByAuthorProvider() {
        return StreamEx.of(
                of(new Author(42L, "a", "b"), Set.of()),
                of(new Author(5L, "a", "b"), Set.of()),
                of(new Author(3L, "a", "b"), Set.of(
                        new Book(2L, "2", "book2",
                                Set.of(
                                        new Author(2L, "fName2", "lName1"),
                                        new Author(3L, "fName3", "lName3")
                                ),
                                Set.of(new Genre(3L, "genre3"))
                        ),
                        new Book(3L, "3", "book3",
                                Set.of(
                                        new Author(1L, "fName1", "lName1"),
                                        new Author(2L, "fName2", "lName1"),
                                        new Author(3L, "fName3", "lName3")
                                ),
                                Set.of(
                                        new Genre(2L, "genre2"),
                                        new Genre(3L, "genre3")
                                )

                        )
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("bookByGenreProvider")
    void findByGenre(Genre genre, Collection<Book> expected) {
        var books = new HashSet<>(bookDAOJdbc.findByGenre(genre));
        assertThat(books, equalTo(expected));
    }

    private static Stream<Arguments> bookByGenreProvider() {
        return StreamEx.of(
                of(new Genre(42L, "rndGenre"), Set.of()),
                of(new Genre(2L, "genre2"),
                        Set.of(
                                new Book(1L, "1", "book1",
                                        Set.of(
                                                new Author(1L, "fName1", "lName1"),
                                                new Author(2L, "fName2", "lName1")
                                        ),
                                        Set.of(
                                                new Genre(1L, "genre1"),
                                                new Genre(2L, "genre2")
                                        )
                                ),
                                new Book(3L, "3", "book3",
                                        Set.of(
                                                new Author(1L, "fName1", "lName1"),
                                                new Author(2L, "fName2", "lName1"),
                                                new Author(3L, "fName3", "lName3")
                                        ),
                                        Set.of(
                                                new Genre(2L, "genre2"),
                                                new Genre(3L, "genre3")
                                        )
                                )
                ))
        );
    }


    @ParameterizedTest
    @MethodSource("bookIdProvider")
    void findById(Long id, Optional<Book> expectedBook) {
        var book = bookDAOJdbc.findById(id);
        assertThat(book, equalTo(expectedBook));
    }

    private static Stream<Arguments> bookIdProvider() {
        return StreamEx.of(
                of(1L, Optional.of(new Book(1L, "1", "book1",
                        Set.of(
                                new Author(1L, "fName1", "lName1"),
                                new Author(2L, "fName2", "lName1")
                        ),
                        Set.of(
                                new Genre(1L, "genre1"),
                                new Genre(2L, "genre2")
                        )
                )),
                of(42L, Optional.empty())
        ));
    }

    @ParameterizedTest
    @MethodSource("bookIsbnProvider")
    void findByIsbn(String isbn, Optional<Book> expectedBook) {
        var book = bookDAOJdbc.findByIsbn(isbn);
        assertThat(book, equalTo(expectedBook));
    }

    private static Stream<Arguments> bookIsbnProvider() {
        return StreamEx.of(
                of("1", Optional.of(new Book(1L, "1", "book1",
                        Set.of(
                                new Author(1L, "fName1", "lName1"),
                                new Author(2L, "fName2", "lName1")
                        ),
                        Set.of(
                                new Genre(1L, "genre1"),
                                new Genre(2L, "genre2")
                        )
                )),
                of("42", Optional.empty())
        ));
    }


    private static Book newBook() {
        return new Book("isbn", "title",
                Set.of(new Author(1L, "fName1", "lName1")),
                Set.of(new Genre(1L, "genre1"))
        );
    }

    @Test
    void save() {
        var book = newBook();
        var initialSize = bookDAOJdbc.fetchAll().size();
        var savedBook = bookDAOJdbc.save(book);

        var finalSize = bookDAOJdbc.fetchAll().size();
        assertThat(finalSize - initialSize, equalTo(1));

        assertThat(savedBook.getTitle(), equalTo(book.getTitle()));
        assertThat(savedBook.getIsbn(), equalTo(book.getIsbn()));
        assertThat(savedBook.getAuthors(), equalTo(book.getAuthors()));
        assertThat(savedBook.getGenres(), equalTo(book.getGenres()));
    }


    @ParameterizedTest
    @MethodSource("bookIsbnProvider")
    void deleteById(Long id, Optional<Book> expectedBook) {
        var initialSize = bookDAOJdbc.fetchAll().size();
        var book = bookDAOJdbc.deleteByObjId(id);

        assertThat(book, equalTo(expectedBook));
        var diffSize = initialSize - bookDAOJdbc.fetchAll().size();

        if (expectedBook.isPresent()) {
            assertThat(diffSize, equalTo(1));
        } else {
            assertThat(diffSize, equalTo(0));
        }
    }

    @Test
    void addAuthor() {
        var bookId = 1L;
        var author = authorDAO.findById(3L).orElseThrow();

        var bookBefore = bookDAOJdbc.findById(bookId);
        assertThat(bookBefore.isPresent(), equalTo(true));
        assertThat(bookBefore.get().getAuthors(), not(hasItem(author)));

        var bookAfter = bookDAOJdbc.addAuthor(bookBefore.get(), author);
        assertThat(bookAfter.getAuthors(), hasItem(author));
        assertThat(bookAfter.getIsbn(), equalTo(bookBefore.get().getIsbn()));
    }

    @Test
    void addGenre() {
        var bookId = 1L;
        var genre = genreDAO.saveObj(new Genre("new genre"));

        var bookBefore = bookDAOJdbc.findById(bookId);
        assertThat(bookBefore.isPresent(), equalTo(true));
        assertThat(bookBefore.get().getGenres(), not(hasItem(genre)));

        var bookAfter = bookDAOJdbc.addGenre(bookBefore.get(), genre);
        assertThat(bookAfter.getGenres(), hasItem(genre));
        assertThat(bookAfter.getIsbn(), equalTo(bookBefore.get().getIsbn()));
    }

}