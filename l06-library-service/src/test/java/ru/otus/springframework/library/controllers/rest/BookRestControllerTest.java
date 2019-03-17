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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.comments.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.delete;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.with;
import static org.assertj.core.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(BookRestController.class)
@ActiveProfiles("rest")
class BookRestControllerTest {

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
    void all() {
        var book = new Book(42L, "123", "title", Set.of(), Set.of());
        var books = List.of(book);

        when(bookService.all()).thenReturn(books);

        var booksRequest = get("/api/v2/books");
        booksRequest.then().statusCode(200);
        verify(bookService).all();

        assertThat(asList(booksRequest.as(Book[].class)), equalTo(books));
    }

    @ParameterizedTest
    @MethodSource("bookProvider")
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

    private static Stream<Arguments> bookProvider() {
        return StreamEx.of(
                Arguments.of("123", Optional.of(dummyBook("123"))),
                Arguments.of("456", Optional.empty())
        );
    }

    @Test
    void addBook() {
        var isbn = "123";
        var title = "asdf";
        var authorIds = List.of(1L, 2L);
        var genres = List.of("a", "b");
        var book = dummyBook(isbn);
        when(bookService.newBook(isbn, title, authorIds, genres)).thenReturn(book);

        var addBookRequest = with()
                .queryParam("isbn", isbn)
                .queryParam("title", title)
                .queryParam("authorIds", authorIds)
                .queryParam("genres", genres)
                .post("/api/v2/book/add");

        addBookRequest.then().statusCode(201);
        assertThat(addBookRequest.as(Book.class), equalTo(book));
        verify(bookService).newBook(isbn, title, authorIds, genres);
    }

    @Test
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
    void addAuthor() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var authorId = 2L;
        when(bookService.addAuthor(isbn, authorId)).thenReturn(book);

        var addAuthorRequest = with()
                .queryParam("authorId", authorId)
                .post("api/v2/book/" + isbn + "/addAuthor");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).addAuthor(isbn, authorId);
    }

    @Test
    void addGenre() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var genre = "g1";
        when(bookService.addGenre(isbn, genre)).thenReturn(book);

        var addAuthorRequest = with()
                .queryParam("genre", genre)
                .post("api/v2/book/" + isbn + "/addGenre");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).addGenre(isbn, genre);
    }

    @Test
    void addComment() {
        var isbn = "123";
        var book = dummyBook(isbn);
        var comment = "comment";
        when(bookService.withIsbn(isbn)).thenReturn(Optional.of(book));

        var addAuthorRequest = with()
                .queryParam("comment", comment)
                .post("api/v2/book/" + isbn + "/addComment");

        addAuthorRequest.then().statusCode(200);
        addAuthorRequest.as(Book.class);
        verify(bookService).withIsbn(isbn);
        verify(commentService).newComment(isbn, comment);
    }

    private static Book dummyBook(String isbn) {
        return new Book(42L, isbn, "title", Set.of(), Set.of());
    }
}