package ru.otus.springframework.library.dao.reactive;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.mongodb.mongock.MongockConfig;
import ru.otus.springframework.library.genres.Genre;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.params.provider.Arguments.of;

@DataMongoTest
@ActiveProfiles({"test", "test-reactive-mongodb"})
@Import({ReactiveSequenceRepository.class, MongockConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReactiveBookMongodbRepositoryTest {

    @Autowired
    private ReactiveBookMongodbRepository bookRepository;

    @Autowired
    private ReactiveAuthorMongodbRepository authorRepository;

    @Autowired
    private ReactiveGenreMongodbRepository genreRepository;

    @Test
    void findAll() {
        var books = bookRepository.findAll().collectList();

        StepVerifier.create(books)
                .assertNext(bs -> {
                    assertThat(bs, hasSize(3));
                    assertThat(StreamEx.of(bs).map(Book::getIsbn).toList(),
                            containsInAnyOrder("1", "2", "3"));
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("bookByAuthorProvider")
    void findByAuthors(Author author, Collection<Book> expected) {
        var books = bookRepository.findByAuthors(Mono.just(author)).collectList();

        StepVerifier.create(books)
                .assertNext(bs -> assertThat(new HashSet<>(bs), equalTo(expected)))
                .verifyComplete();
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
        var books = bookRepository.findByGenres(Mono.just(genre)).collectList();

        StepVerifier.create(books)
                .assertNext(bs -> assertThat(new HashSet<>(bs), equalTo(expected)))
                .verifyComplete();
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
        var book = bookRepository.findById(id);

        if (expectedBook.isPresent()) {
            StepVerifier.create(book)
                    .assertNext(bk -> assertThat(bk, equalTo(expectedBook.get())))
                    .verifyComplete();
        } else {
            StepVerifier.create(book).verifyComplete();
        }
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
                        ))),
                of(42L, Optional.empty())
        );
    }


    @ParameterizedTest
    @MethodSource("bookIsbnProvider")
    void findByIsbn(String isbn, Optional<Book> expectedBook) {
        var book = bookRepository.findByIsbn(isbn);

        if (expectedBook.isPresent()) {
            StepVerifier.create(book)
                    .assertNext(bk -> assertThat(bk, equalTo(expectedBook.get())))
                    .verifyComplete();
        } else {
            StepVerifier.create(book).verifyComplete();
        }
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
                        ))),
                of("42", Optional.empty())
        );
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
        var initialSize = bookRepository.findAll().collectList().block().size();
        var savedBook = bookRepository.saveObj(book);

        StepVerifier.create(savedBook)
                .assertNext(bk -> {
                    assertThat(bk.getTitle(), equalTo(book.getTitle()));
                    assertThat(bk.getIsbn(), equalTo(book.getIsbn()));
                    assertThat(bk.getAuthors(), equalTo(book.getAuthors()));
                    assertThat(bk.getGenres(), equalTo(book.getGenres()));
                })
                .verifyComplete();

        StepVerifier.create(bookRepository.findAll().collectList())
                .assertNext(bs -> assertThat(bs.size() - initialSize, equalTo(1)))
                .expectComplete()
                .verify();
    }


    @ParameterizedTest
    @MethodSource("bookIsbnProvider")
    void deleteById(Long id, Optional<Book> expectedBook) {
        var initialSize = bookRepository.findAll().collectList().block().size();
        var book = bookRepository.deleteByObjId(id);

        if (expectedBook.isPresent()) {
            StepVerifier.create(book)
                    .assertNext(bk -> assertThat(bk, equalTo(expectedBook.get())))
                    .verifyComplete();
        } else {
            StepVerifier.create(book).verifyComplete();
        }

        StepVerifier.create(bookRepository.findAll().collectList())
                .assertNext(bs -> assertThat(initialSize - bs.size(), equalTo(
                        expectedBook.isPresent() ? 1 : 0)
                ))
                .verifyComplete();
    }

    @Test
    void addAuthor() {
        var bookId = 1L;
        var author = authorRepository.findById(3L);

        var bookBefore = bookRepository.findById(bookId).block();
        assertThat(StreamEx.of(bookBefore.getAuthors())
                        .findAny(a -> a.getId().equals(3L))
                        .isPresent(),
                equalTo(false));

        var bookAfter = bookRepository.addAuthor(bookBefore, author);

        StepVerifier.create(bookAfter)
                .assertNext(bk -> {
                    assertThat(bk.getIsbn(), equalTo(bookBefore.getIsbn()));
                    assertThat(StreamEx.of(bk.getAuthors())
                            .findAny(a -> a.getId().equals(3L))
                            .isPresent(),
                            equalTo(true)
                    );
                })
                .verifyComplete();
    }

    @Test
    void addGenre() {
        var bookId = 1L;
        var genre = genreRepository.saveObj(new Genre("new genre"));

        var bookBefore = bookRepository.findById(bookId).block();
        assertThat(StreamEx.of(bookBefore.getGenres())
                .findAny(g -> g.getName().equals("new genre"))
                .isPresent(),
                equalTo(false)
        );

        var bookAfter = bookRepository.addGenre(bookBefore, genre);
        StepVerifier.create(bookAfter)
                .assertNext(bk -> {
                    assertThat(bk.getIsbn(), equalTo(bookBefore.getIsbn()));
                    assertThat(StreamEx.of(bk.getGenres())
                                    .findAny(g -> g.getName().equals("new genre"))
                                    .isPresent(),
                            equalTo(true)
                    );
                })
                .verifyComplete();
    }

}