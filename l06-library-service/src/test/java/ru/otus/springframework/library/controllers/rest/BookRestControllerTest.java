package ru.otus.springframework.library.controllers.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.CommentService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.assertj.core.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(BookRestController.class)
@ActiveProfiles({"rest", "test-mongodb"})
class BookRestControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private BookService bookService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mvc);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void all() {
        var book = new Book(42L, "123", "title", Set.of(), Set.of());
        var books = List.of(book);

        when(bookService.all()).thenReturn(books);

        var booksRequest = get("/api/v2/book");
        booksRequest.then().statusCode(200);
        verify(bookService).all();

        assertThat(asList(booksRequest.as(Book[].class)), equalTo(books));
    }

    @Test
    void all302() {
        get("/api/v2/book")
                .then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @ParameterizedTest
    @MethodSource("bookProvider")
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void book(String isbn, Optional<Book> book) {
        when(bookService.withIsbn(isbn)).thenReturn(book);

        var booksRequest = get("/api/v2/book/" + isbn);

        if (book.isPresent()) {
            booksRequest.then().statusCode(200);
            assertThat(booksRequest.as(Book.class), equalTo(book.get()));
        } else {
            booksRequest.then().statusCode(404);
        }

        verify(bookService).withIsbn(isbn);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void book403() {
        get("/api/v2/book/123")
                .then().statusCode(403);
    }

    @Test
    void book302() {
        get("/api/v2/book/123")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    private static Stream<Arguments> bookProvider() {
        return StreamEx.of(
                Arguments.of("123", Optional.of(dummyBook("123"))),
                Arguments.of("456", Optional.empty())
        );
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addBook() {
        var isbn = "123";
        var title = "Title";
        var authorIds = List.of(1L, 2L);
        var genres = List.of("a", "b");
        var book = dummyBook(isbn);
        when(bookService.newBook(isbn, title, authorIds, genres)).thenReturn(book);

        var addBookRequest = with()
                .queryParam("isbn", isbn)
                .queryParam("title", title)
                .queryParam("authorIds", authorIds)
                .queryParam("genres", genres)
                .post("/api/v2/book");

        addBookRequest.then().statusCode(201);
        assertThat(addBookRequest.as(Book.class), equalTo(book));
        verify(bookService).newBook(isbn, title, authorIds, genres);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addBook403() {
        var isbn = "123";
        var title = "Title";
        var authorIds = List.of(1L, 2L);
        var genres = List.of("a", "b");

        with()
                .queryParam("isbn", isbn)
                .queryParam("title", title)
                .queryParam("authorIds", authorIds)
                .queryParam("genres", genres)
                .post("/api/v2/book")
                .then().statusCode(403);
    }

    @Test
    void addBook302() {
        var isbn = "123";
        var title = "Title";
        var authorIds = List.of(1L, 2L);
        var genres = List.of("a", "b");

        with()
                .queryParam("isbn", isbn)
                .queryParam("title", title)
                .queryParam("authorIds", authorIds)
                .queryParam("genres", genres)
                .post("/api/v2/book")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));

    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void deleteBook() {
        var isbn = "123";
        var book = dummyBook(isbn);
        when(bookService.removeBook(isbn)).thenReturn(Optional.of(book));

        var deleteBookRequest = delete("/api/v2/book/" + isbn);
        deleteBookRequest.then().statusCode(200);
        assertThat(deleteBookRequest.as(Book.class), equalTo(book));
        verify(bookService).removeBook(isbn);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void deleteBook403() {
        var isbn = "123";
        delete("/api/v2/book/" + isbn)
                .then().statusCode(403);
    }

    @Test
    void deleteBook302() {
        var isbn = "123";
        delete("/api/v2/book/" + isbn)
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addAuthor() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var authorId = 2L;
        when(bookService.addAuthor(isbn, authorId)).thenReturn(book);

        var addAuthorRequest = with()
                .queryParam("authorId", authorId)
                .post("api/v2/book/" + isbn + "/authors");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).addAuthor(isbn, authorId);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addAuthor403() {
        var isbn = "123";
        with()
                .queryParam("authorId", 1L)
                .post("api/v2/book/" + isbn + "/authors")
                .then().statusCode(403);
    }

    @Test
    void addAuthor302() {
        var isbn = "123";
        with()
                .queryParam("authorId", 1L)
                .post("api/v2/book/" + isbn + "/authors")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addGenre() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var genre = "g1";
        when(bookService.addGenre(isbn, genre)).thenReturn(book);

        var addAuthorRequest = with()
                .queryParam("genre", genre)
                .post("api/v2/book/" + isbn + "/genres");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).addGenre(isbn, genre);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addGenre403() {
        var isbn = "123";
        var genre = "g1";

        with()
                .queryParam("genre", genre)
                .post("api/v2/book/" + isbn + "/genres")
                .then().statusCode(403);
    }

    @Test
    void addGenre302() {
        var isbn = "123";
        var genre = "g1";

        with()
                .queryParam("genre", genre)
                .post("api/v2/book/" + isbn + "/genres")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addComment() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var comment = "comment";
        when(bookService.withIsbn(isbn)).thenReturn(Optional.of(book));

        var addAuthorRequest = with()
                .queryParam("comment", comment)
                .post("api/v2/book/" + isbn + "/comments");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).withIsbn(isbn);
        verify(commentService).newComment(isbn, comment);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addComment403() {
        var isbn = "123";
        var comment = "comment";

        with()
                .queryParam("comment", comment)
                .post("api/v2/book/" + isbn + "/comments")
                .then().statusCode(403);
    }

    @Test
    void addComment302() {
        var isbn = "123";
        var comment = "comment";

        with()
                .queryParam("comment", comment)
                .post("api/v2/book/" + isbn + "/comments")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
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
        when(commentService.commentsFor(isbn)).thenReturn(comments);

        var commentsRequest = get("/api/v2/book/" + isbn + "/comments");

        commentsRequest.then().statusCode(200);
        verify(commentService).commentsFor(isbn);
        assertThat(asList(commentsRequest.as(Comment[].class)), equalTo(comments));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void comments403() {
        var isbn = "qwe";
         get("/api/v2/book/" + isbn + "/comments")
                 .then().statusCode(403);
    }

    @Test
    void comments302() {
        var isbn = "qwe";
        get("/api/v2/book/" + isbn + "/comments")
                .then().statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    private static Book dummyBook(String isbn) {
        return new Book(42L, isbn, "title", Set.of(), Set.of());
    }
}