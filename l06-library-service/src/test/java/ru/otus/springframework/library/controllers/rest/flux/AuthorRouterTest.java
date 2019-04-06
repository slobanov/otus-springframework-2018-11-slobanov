package ru.otus.springframework.library.controllers.rest.flux;

import io.restassured.module.webtestclient.RestAssuredWebTestClient;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.flux.AuthorServiceFlux;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.flux.BookServiceFlux;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.delete;
import static io.restassured.module.webtestclient.RestAssuredWebTestClient.get;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@WebFluxTest
@ActiveProfiles("flux")
@Import({AuthorRouter.class, AuthorHandler.class})
class AuthorRouterTest {

    @MockBean
    private BookServiceFlux bookService;

    @MockBean
    private AuthorServiceFlux authorService;

    @Autowired
    RouterFunction<ServerResponse> authorRouterFunction;

    @BeforeEach
    void init() {
        RestAssuredWebTestClient.standaloneSetup(authorRouterFunction);
    }

    @ParameterizedTest
    @MethodSource("authorsProvider")
    void all(List<Author> authors) {
        when(authorService.all()).thenReturn(Flux.fromIterable(authors));
        var authorsRequest = get("/api/v2/author");
        authorsRequest.then().statusCode(200);
        var responseAuthors = authorsRequest.as(Author[].class);
        assertThat(asList(responseAuthors), equalTo(authors));
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
    void addAuthor() {
        var firstName = "fName";
        var lastName = "lName";
        var expectedAuthor = new Author(42L, firstName, lastName);
        when(authorService.newAuthor(firstName, lastName)).thenReturn(Mono.just(expectedAuthor));

        var client = WebTestClient.bindToRouterFunction(authorRouterFunction).build();
        var author = client.post().uri("/api/v2/author").body(
                fromFormData("firstName", firstName).with("lastName", lastName)
        ).exchange()
         .expectStatus().isCreated()
         .expectBody(Author.class)
         .returnResult().getResponseBody();

        assertThat(author, equalTo(expectedAuthor));
        verify(authorService).newAuthor(firstName, lastName);
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void author(Long id, Author author) {
        when(authorService.withId(id)).thenReturn(Mono.just(author));
        var authorRequest = get("/api/v2/author/" + id );
        authorRequest.then().statusCode(200);
        var respAuthor = authorRequest.as(Author.class);
        assertThat(respAuthor, equalTo(author));
    }

    @Test
    void authorNoAuthor() {
        var id = 42L;
        when(authorService.withId(id)).thenReturn(Mono.empty());
        var authorRequest = get("/api/v2/author/" + id );
        authorRequest.then().statusCode(404);
    }

    private static Stream<Arguments> authorProvider() {
        return StreamEx.of(
                of(1L, new Author(1L, "fName1", "lName1")),
                of(2L, new Author(2L, "fName2", "lName1")),
                of(4L, new Author(4L, "fName4", "lName4"))
        );
    }

    @Test
    void deleteAuthor() {
        var id = 42L;
        var author = new Author(id, "fName", "lName");
        when(authorService.removeAuthor(id)).thenReturn(Mono.just(author));

        var authorRemoveRequest = delete("/api/v2/author/" + id);

        authorRemoveRequest.then().statusCode(200);
        assertThat(authorRemoveRequest.as(Author.class), equalTo(author));
        verify(authorService).removeAuthor(id);
    }

    @Test
    void authorBooks() {
        var authorId = 42L;
        var book = new Book(1L, "123", "title", Set.of(), Set.of());
        when(bookService.writtenBy(authorId)).thenReturn(Flux.just(book));

        var authorBooksRequest = get("/api/v2/author/" + authorId + "/books");

        authorBooksRequest.then().statusCode(200);
        assertThat(asList(authorBooksRequest.as(Book[].class)), equalTo(List.of(book)));
        verify(bookService).writtenBy(authorId);
    }

}