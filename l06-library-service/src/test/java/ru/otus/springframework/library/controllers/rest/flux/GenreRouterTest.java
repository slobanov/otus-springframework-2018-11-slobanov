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
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.flux.GenreServiceFlux;

import java.util.List;
import java.util.Set;

import static io.restassured.module.webtestclient.RestAssuredWebTestClient.delete;
import static io.restassured.module.webtestclient.RestAssuredWebTestClient.get;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

@WebFluxTest
@ActiveProfiles("flux")
@Import({GenreRouter.class, GenreHandler.class})
class GenreRouterTest {

    @MockBean
    private BookServiceFlux bookService;

    @MockBean
    private GenreServiceFlux genreService;

    @Autowired
    private RouterFunction<ServerResponse> genreRouterFunction;

    @BeforeEach
    void init() {
        RestAssuredWebTestClient.standaloneSetup(genreRouterFunction);
    }

    @Test
    void all() {
        var genres = List.of(new Genre("1"), new Genre("2"));
        when(genreService.all()).thenReturn(Flux.fromIterable(genres));

        var genresRequest =  get("/api/v2/genre");
        genresRequest.then().statusCode(200);
        var responseAuthors = genresRequest.as(Genre[].class);
        assertThat(asList(responseAuthors), equalTo(genres));
    }

    @Test
    void addGenre() {
        var genre = "123";
        var genreObj = new Genre(genre);
        when(genreService.newGenre(genre)).thenReturn(Mono.just(genreObj));

        var client = WebTestClient.bindToRouterFunction(genreRouterFunction).build();
        var resGenre = client.post().uri("/api/v2/genre")
                .body(fromFormData("genre", genre))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Genre.class)
                .returnResult().getResponseBody();

        verify(genreService).newGenre(genre);
        assertThat(resGenre, equalTo(genreObj));
    }

    @Test
    void deleteGenre() {
        var genre = "123";
        var genreObj = new Genre(genre);
        when(genreService.removeGenre(genre)).thenReturn(Mono.just(genreObj));

        var genreRemoveRequest = delete("/api/v2/genre/" + genre);

        genreRemoveRequest.then().statusCode(200);
        assertThat(genreRemoveRequest.as(Genre.class), equalTo(genreObj));
        verify(genreService).removeGenre(genre);
    }

    @Test
    void booksOfGenre() {
        var genre = "123";
        var book = new Book(1L, "123", "title", Set.of(), Set.of());
        when(bookService.ofGenre(genre)).thenReturn(Flux.just(book));

        var authorBooksRequest = get("/api/v2/genre/" + genre + "/books");

        authorBooksRequest.then().statusCode(200);
        assertThat(asList(authorBooksRequest.as(Book[].class)), equalTo(List.of(book)));
        verify(bookService).ofGenre(genre);
    }

}