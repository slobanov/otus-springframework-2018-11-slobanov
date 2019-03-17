package ru.otus.springframework.library.controllers.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.delete;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.get;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.with;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(GenreRestController.class)
@ActiveProfiles("rest")
class GenreRestControllerTest {

    @MockBean
    private GenreService genreService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mvc);
    }

    @Test
    void all() {
        var genres = List.of(new Genre("1"), new Genre("2"));
        when(genreService.all()).thenReturn(genres);

        var genresRequest =  get("/api/v2/genres");
        genresRequest.then().statusCode(200);
        var responseAuthors = genresRequest.as(Genre[].class);
        assertThat(asList(responseAuthors), equalTo(genres));
    }

    @Test
    void addGenre() {
        var genre = "123";
        var genreObj = new Genre(genre);
        when(genreService.newGenre(genre)).thenReturn(genreObj);

        var addGenreRequest = with()
                .queryParam("genre", genre)
                .post("/api/v2/genre/add");

        addGenreRequest.then().statusCode(201);
        verify(genreService).newGenre(genre);
        assertThat(addGenreRequest.as(Genre.class), equalTo(genreObj));
    }

    @Test
    void deleteGenre() {
        var genre = "123";
        var genreObj = new Genre(genre);
        when(genreService.removeGenre(genre)).thenReturn(Optional.of(genreObj));

        var genreRemoveRequest = delete("/api/v2/genre/" + genre);

        genreRemoveRequest.then().statusCode(200);
        assertThat(genreRemoveRequest.as(Genre.class), equalTo(genreObj));
        verify(genreService).removeGenre(genre);
    }

    @Test
    void booksOfGenre() {
        var genre = "123";
        var book = new Book(1L, "123", "title", Set.of(), Set.of());
        when(bookService.ofGenre(genre)).thenReturn(List.of(book));

        var authorBooksRequest = get("/api/v2/genre/" + genre + "/books");

        authorBooksRequest.then().statusCode(200);
        assertThat(asList(authorBooksRequest.as(Book[].class)), equalTo(List.of(book)));
        verify(bookService).ofGenre(genre);
    }
}