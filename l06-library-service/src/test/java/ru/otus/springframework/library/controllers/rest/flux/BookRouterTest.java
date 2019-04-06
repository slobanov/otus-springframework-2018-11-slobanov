package ru.otus.springframework.library.controllers.rest.flux;


import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.flux.BookServiceFlux;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.flux.CommentServiceFlux;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.*;
import static java.lang.String.*;
import static org.assertj.core.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@WebFluxTest
@ActiveProfiles("flux")
@Import({BookHandler.class, BookRouter.class})
class BookRouterTest {

    @MockBean
    private BookServiceFlux bookService;

    @MockBean
    private CommentServiceFlux commentService;

    @Autowired
    private RouterFunction<ServerResponse> bookRouterFunction;

    @BeforeEach
    void init() {
        RestAssuredWebTestClient.standaloneSetup(bookRouterFunction);
    }

    @Test
    void all() {
        var book = dummyBook("123");
        var books = List.of(book);

        when(bookService.all()).thenReturn(Flux.fromIterable(books));

        var booksRequest = get("/api/v2/book");
        booksRequest.then().statusCode(200);
        verify(bookService).all();

        assertThat(asList(booksRequest.as(Book[].class)), equalTo(books));
    }

    @Test
    void book() {
        var isbn = "123";
        var book = dummyBook(isbn);
        when(bookService.withIsbn(isbn)).thenReturn(Mono.just(book));

        var booksRequest = get("/api/v2/book/" + isbn);
        booksRequest.then().statusCode(200);
        assertThat(booksRequest.as(Book.class), equalTo(book));
        verify(bookService).withIsbn(isbn);
    }

    @Test
    void bookNotFound() {
        var isbn = "123";
        when(bookService.withIsbn(isbn)).thenReturn(Mono.empty());

        get("/api/v2/book/" + isbn).then().statusCode(404);
        verify(bookService).withIsbn(isbn);
    }

    @Test
    void addBook() {
        var isbn = "123";
        var title = "Title";
        var authorIds = List.of(1L);
        var genres = List.of("a");
        var book = dummyBook(isbn);
        when(bookService.newBook(isbn, title, authorIds, genres)).thenReturn(Mono.just(book));

        var client = WebTestClient.bindToRouterFunction(bookRouterFunction).build();
        var actualBook = client.post().uri("/api/v2/book/")
                .body(
                        fromFormData("isbn", isbn)
                        .with("title", title)
                        .with("authorIds", "1")
                        .with("genres", "a")
                )
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .returnResult().getResponseBody();

        assertThat(actualBook, equalTo(book));
        verify(bookService).newBook(isbn, title, authorIds, genres);
    }

    @Test
    void deleteBook() {
        var isbn = "123";
        var book = dummyBook(isbn);
        when(bookService.removeBook(isbn)).thenReturn(Mono.just(book));

        var deleteBookRequest = delete("/api/v2/book/" + isbn);
        deleteBookRequest.then().statusCode(200);
        assertThat(deleteBookRequest.as(Book.class), equalTo(book));
        verify(bookService).removeBook(isbn);
    }

    @Test
    void addAuthor() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var authorId = 2L;
        when(bookService.addAuthor(isbn, authorId)).thenReturn(Mono.just(book));

        var client = WebTestClient.bindToRouterFunction(bookRouterFunction).build();
        var actualBook = client.post().uri("/api/v2/book/" + isbn + "/authors")
                .body(fromFormData("authorId", valueOf(authorId)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .returnResult().getResponseBody();

        assertThat(actualBook, equalTo(book));
        verify(bookService).addAuthor(isbn, authorId);
    }

    @Test
    void addGenre() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var genre = "g1";
        when(bookService.addGenre(isbn, genre)).thenReturn(Mono.just(book));

        var client = WebTestClient.bindToRouterFunction(bookRouterFunction).build();
        var actualBook = client.post().uri("/api/v2/book/" + isbn + "/genres")
                .body(fromFormData("genre", genre))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .returnResult().getResponseBody();

        assertThat(actualBook, equalTo(book));
        verify(bookService).addGenre(isbn, genre);
    }

    @Test
    void addComment() {
        var isbn = "123";
        var commentText = "comment";
        var comment = mock(Comment.class);
        var book = dummyBook(isbn);
        when(comment.getBook()).thenReturn(book);
        when(commentService.newComment(isbn, commentText)).thenReturn(Mono.just(comment));

        var client = WebTestClient.bindToRouterFunction(bookRouterFunction).build();
        var actualBook = client.post().uri("/api/v2/book/" + isbn + "/comments")
                .body(fromFormData("comment", commentText))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .returnResult().getResponseBody();

        assertThat(actualBook, equalTo(book));
        verify(commentService).newComment(isbn, commentText);
    }

    @Test
    void comments() {
        var isbn = "qwe";
        var comment = new Comment(
                1L,
                new Book(
                        42L,
                        isbn,
                        "title",
                        Set.of(),
                        Set.of()
                ),
                "123",
                new Date()
        );
        var comments = List.of(comment);
        when(commentService.commentsFor(isbn)).thenReturn(Flux.fromIterable(comments));

        var commentsRequest = get("/api/v2/book/" + isbn + "/comments");

        commentsRequest.then().statusCode(200);
        verify(commentService).commentsFor(isbn);
        assertThat(asList(commentsRequest.as(Comment[].class)), equalTo(comments));
    }

    private static Book dummyBook(String isbn) {
        return new Book(42L, isbn, "title", Set.of(), Set.of());
    }
}

