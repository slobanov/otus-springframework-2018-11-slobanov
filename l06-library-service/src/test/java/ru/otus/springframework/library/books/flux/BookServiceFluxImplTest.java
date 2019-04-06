package ru.otus.springframework.library.books.flux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.reactive.ReactiveAuthorMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveBookMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveGenreMongodbRepository;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceFluxImplTest {

    @Mock
    private ReactiveGenreMongodbRepository genreRepository;

    @Mock
    private ReactiveAuthorMongodbRepository authorRepository;

    @Mock
    private ReactiveBookMongodbRepository bookRepository;

    @InjectMocks
    private BookServiceFluxImpl bookService;

    @Test
    void all() {
        var expectedBook = mock(Book.class);
        when(bookRepository.findAll()).thenReturn(Flux.just(expectedBook));

        StepVerifier.create(bookService.all())
                .assertNext(book -> assertThat(book, equalTo(expectedBook)))
                .verifyComplete();

        verify(bookRepository).findAll();
    }

    private static List<Book> someBooks() {
        return List.of(mock(Book.class), mock(Book.class));
    }

    @Test
    void writtenBy() {
        var authorId = 1L;
        var author = mock(Author.class);
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));

        var expectedBooks = someBooks();
        when(bookRepository.findByAuthors(any(Mono.class))).thenReturn(Flux.fromIterable(expectedBooks));

        StepVerifier.create(bookService.writtenBy(authorId).collectList())
                .assertNext(books -> assertThat(books, equalTo(expectedBooks)))
                .verifyComplete();
    }

    @Test
    void ofGenre() {
        var genreString = "genre";
        var genre = mock(Genre.class);
        when(genreRepository.findByName(genreString)).thenReturn(Mono.just(genre));

        var expectedBooks = someBooks();
        when(bookRepository.findByGenres(any(Mono.class))).thenReturn(Flux.fromIterable(expectedBooks));

        StepVerifier.create(bookService.ofGenre(genreString).collectList())
                .assertNext(books -> assertThat(books, equalTo(expectedBooks)))
                .verifyComplete();
    }

    @Test
    void withIsbn() {
        var isbn = "123";
        var expectedBook = mock(Book.class);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.just(expectedBook));

        StepVerifier.create(bookService.withIsbn(isbn))
                .assertNext(book -> assertThat(book, equalTo(expectedBook)))
                .verifyComplete();
    }

    @Test
    void newBook() {
        var authorId = 1L;
        var author = mock(Author.class);
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));

        var genreName = "genre";
        var genre = mock(Genre.class);

        when(genreRepository.findByName(genreName)).thenReturn(Mono.just(genre));
        when(genreRepository.saveObj(any(Genre.class))).thenReturn(Mono.empty());

        var isbn = "isbn";
        var title = "title";
        var book = new Book(
                isbn,
                title,
                Set.of(author),
                Set.of(genre)
        );

        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.empty());
        when(bookRepository.saveObj(any(Book.class))).thenReturn(Mono.just(book));

        var resultBook = bookService.newBook(
                isbn,
                title,
                List.of(authorId),
                List.of(genreName)
        );

        StepVerifier.create(resultBook)
                .assertNext(b -> assertThat(b, equalTo(book)))
                .verifyComplete();
    }

    @Test
    void newBookAlreadyExists() {
        var isbn = "isbn";
        var book = mock(Book.class);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.just(book));

        StepVerifier.create(bookService.newBook(isbn, "", List.of(), List.of()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void newBookNoAuthor() {
        var authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(Mono.empty());

        var isbn = "123";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.empty());

        StepVerifier.create(bookService.newBook(isbn, "", List.of(authorId), List.of()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void newBookNoGenre() {
        var authorId = 1L;
        var author = mock(Author.class);
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));

        var genreName = "genre";
        var genre = mock(Genre.class);

        when(genreRepository.findByName(genreName)).thenReturn(Mono.empty());
        when(genreRepository.saveObj(new Genre(genreName))).thenReturn(Mono.just(genre));

        var isbn = "isbn";
        var title = "title";
        var book = new Book(
                isbn,
                title,
                Set.of(author),
                Set.of(genre)
        );

        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.empty());
        when(bookRepository.saveObj(any(Book.class))).thenReturn(Mono.just(book));

        var resultBook = bookService.newBook(
                isbn,
                title,
                List.of(authorId),
                List.of(genreName)
        );

        StepVerifier.create(resultBook)
                .assertNext(b -> assertThat(b, equalTo(book)))
                .verifyComplete();

    }

    @Test
    void removeBook() {
        var isbn = "123";
        var id = 1L;
        var book = mock(Book.class);
        when(book.getId()).thenReturn(id);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.just(book));
        when(bookRepository.deleteByObjId(id)).thenReturn(Mono.just(book));

        StepVerifier.create(bookService.removeBook(isbn))
                .assertNext(b -> assertThat(b, equalTo(book)))
                .verifyComplete();

        verify(bookRepository).deleteByObjId(id);
        verify(bookRepository).findByIsbn(isbn);
    }

    @Test
    void addAuthor() {
        var bookId = 1L;
        var bookIsbn = "isbn";
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of());
        var authorId = 2L;
        var author = new Author(authorId, "fName", "lName");

        when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Mono.just(book));
        when(bookRepository.addAuthor(eq(book), any(Mono.class))).thenReturn(Mono.just(book));
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(author));

        StepVerifier.create(bookService.addAuthor(bookIsbn, authorId))
                .assertNext(b -> assertThat(b, equalTo(book)))
                .verifyComplete();

        verify(bookRepository).findByIsbn(bookIsbn);
        verify(bookRepository).addAuthor(eq(book), any(Mono.class));
        verify(authorRepository).findById(authorId);
    }

    @Test
    void addGenre() {
        var bookIsbn = "isbn";
        var bookId = 1L;
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of());
        var genre = "genre";
        var genreObj = new Genre(2L, genre);

        when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Mono.just(book));
        when(bookRepository.addGenre(eq(book), any(Mono.class))).thenReturn(Mono.just(book));
        when(genreRepository.findByName(genre)).thenReturn(Mono.just(genreObj));
        when(genreRepository.saveObj(any(Genre.class))).thenReturn(Mono.empty());

        StepVerifier.create(bookService.addGenre(bookIsbn, genre))
                .assertNext(b -> assertThat(b, equalTo(book)))
                .verifyComplete();

        verify(bookRepository).findByIsbn(bookIsbn);
        verify(bookRepository).addGenre(eq(book), any(Mono.class));
    }
}