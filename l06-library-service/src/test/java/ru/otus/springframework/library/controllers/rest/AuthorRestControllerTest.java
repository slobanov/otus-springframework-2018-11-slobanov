package ru.otus.springframework.library.controllers.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import one.util.streamex.EntryStream;
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
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthorRestController.class)
@ActiveProfiles({"rest", "test-mongodb"})
class AuthorRestControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mvc);
    }

    @ParameterizedTest
    @MethodSource("authorsProvider")
    @WithMockUser(username = "test_user", password = "test_password")
    void all(List<Author> authors) {
        when(authorService.all()).thenReturn(authors);
        var authorsRequest = get("/api/v2/author");
        authorsRequest.then().statusCode(200);
        var responseAuthors = authorsRequest.as(Author[].class);
        assertThat(asList(responseAuthors), equalTo(authors));
    }

    @Test
    void all302() {
        get("/api/v2/author").then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    private static Stream<Arguments> authorsProvider() {
        return StreamEx.of(
                of(List.of(new Author(1L, "fName1", "lName1"))),
                of(List.of(
                        new Author(2L, "fName2", "lName1"),
                        new Author(4L, "fName4", "lName4")
                ))
        );
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_AUTHOR")
    void addAuthor() {
        var firstName = "fName";
        var lastName = "lName";
        var author = new Author(42L, firstName, lastName);
        when(authorService.newAuthor(firstName, lastName)).thenReturn(author);

        var addAuthorRequest = with()
                .queryParam("firstName", firstName)
                .queryParam("lastName", lastName)
                .post("/api/v2/author");

        addAuthorRequest.then().statusCode(201);
        assertThat(addAuthorRequest.as(Author.class), equalTo(author));
        verify(authorService).newAuthor(firstName, lastName);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addAuthor403() {
        var firstName = "fName";
        var lastName = "lName";

        with()
                .queryParam("firstName", firstName)
                .queryParam("lastName", lastName)
                .post("/api/v2/author")
                .then().statusCode(403);
    }

    @Test
    void addAuthor302() {
        var firstName = "fName";
        var lastName = "lName";

        with()
                .queryParam("firstName", firstName)
                .queryParam("lastName", lastName)
                .post("/api/v2/author")
                .then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_AUTHOR")
    void author(Long id, Optional<Author> author) {
        when(authorService.withId(id)).thenReturn(author);
        var authorRequest = get("/api/v2/author/" + id );
        if (author.isPresent()) {
            authorRequest.then().statusCode(200);
            var respAuthor = authorRequest.as(Author.class);
            assertThat(respAuthor, equalTo(author.get()));
        } else {
            authorRequest.then().statusCode(404);
        }
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void author403() {
        get("/api/v2/author/123")
                .then().statusCode(403);
    }

    @Test
    void author302() {
        get("/api/v2/author/123")
                .then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    private static Stream<Arguments> authorProvider() {
        return EntryStream.of(
                1L, new Author(1L, "fName1", "lName1"),
                2L, new Author(2L, "fName2", "lName1"),
                4L, new Author(4L, "fName4", "lName4"),
                42L, null
        ).mapValues(Optional::ofNullable).mapToValue((id, a) -> of(id, a)).values();
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_AUTHOR")
    void deleteAuthor() {
        var id = 42L;
        var author = new Author(id, "fName", "lName");
        when(authorService.removeAuthor(id)).thenReturn(Optional.of(author));

        var authorRemoveRequest = delete("/api/v2/author/" + id);

        authorRemoveRequest.then().statusCode(200);
        assertThat(authorRemoveRequest.as(Author.class), equalTo(author));
        verify(authorService).removeAuthor(id);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void deleteAuthor403() {
        delete("/api/v2/author/42")
                .then().statusCode(403);
    }

    @Test
    void deleteAuthor302() {
        delete("/api/v2/author/42")
                .then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_AUTHOR")
    void authorBooks() {
        var authorId = 42L;
        var book = new Book(1L, "123", "title", Set.of(), Set.of());
        when(bookService.writtenBy(authorId)).thenReturn(List.of(book));

        var authorBooksRequest = get("/api/v2/author/" + authorId + "/books");

        authorBooksRequest.then().statusCode(200);
        assertThat(asList(authorBooksRequest.as(Book[].class)), equalTo(List.of(book)));
        verify(bookService).writtenBy(authorId);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void authorBooks403() {
        var authorId = 42L;
        get("/api/v2/author/" + authorId + "/books")
                .then().statusCode(403);
    }

    @Test
    void authorBooks302() {
        var authorId = 42L;
        get("/api/v2/author/" + authorId + "/books")
                .then()
                .statusCode(302)
                .header("Location", is("http://localhost/login.html"));
    }
}