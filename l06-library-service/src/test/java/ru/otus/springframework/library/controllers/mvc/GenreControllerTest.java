package ru.otus.springframework.library.controllers.mvc;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
@ActiveProfiles({"mvc", "test-mongodb"})
class GenreControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void all() throws Exception {
        var genres = List.of(mock(Genre.class), mock(Genre.class));
        when(genreService.all()).thenReturn(genres);

        var modelAndView = mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andReturn()
                .getModelAndView();

        verify(genreService).all();
        assertThat(modelAndView.getViewName(), equalTo("genres"));
        assertThat(modelAndView.getModel(), hasEntry("genres", genres));
    }

    @Test
    void all302() throws Exception {
        mvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_GENRE")
    void genre() throws Exception {
        var genre = "gew 1";
        var books = List.of(mock(Book.class));
        when(bookService.ofGenre("gew 1")).thenReturn(books);

        var result = mvc.perform(get("/genre/" + genre));
        var modelAndView = result.andReturn().getModelAndView();

        result.andExpect(status().isOk());
        assertThat(modelAndView.getViewName(), equalTo("genre"));
        assertThat(modelAndView.getModel(), hasEntry("genre", genre));
        assertThat(modelAndView.getModel(), hasEntry("books", books));
        verify(bookService).ofGenre(genre);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void genre403() throws Exception {
        var genre = "gew 1";
        mvc.perform(get("/genre/" + genre))
                .andExpect(status().isForbidden());
    }

    @Test
    void genre302() throws Exception {
        var genre = "gew 1";
        mvc.perform(get("/genre/" + genre))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_GENRE")
    void addGenre() throws Exception {
        var genre = "asd 123";
        var genreObj = new Genre(1L, genre);
        when(genreService.newGenre(genre)).thenReturn(genreObj);

        var modelAndView = mvc.perform(post("/genre/add")
                .param("genre", genre)
        ).andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();

        verify(genreService).newGenre(genre);
        assertThat(modelAndView.getViewName(), Matchers.equalTo("redirect:/genre/" + genre));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addGenre403() throws Exception {
        var genre = "asd 123";
        mvc.perform(post("/genre/add")
                .param("genre", genre)
        ).andExpect(status().isForbidden());
    }

    @Test
    void addGenre302() throws Exception {
        var genre = "asd 123";
        mvc.perform(post("/genre/add")
                .param("genre", genre)
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_GENRE")
    void delete() throws Exception {
        var genre = "asf 1";
        var modelAndView = mvc.perform(post("/genre/" + genre + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();

        verify(genreService).removeGenre(genre);
        assertThat(modelAndView.getViewName(), Matchers.equalTo("redirect:/genres"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void delete403() throws Exception {
        var genre = "asf 1";
        mvc.perform(post("/genre/" + genre + "/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete302() throws Exception {
        var genre = "asf 1";
        mvc.perform(post("/genre/" + genre + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();
    }
}