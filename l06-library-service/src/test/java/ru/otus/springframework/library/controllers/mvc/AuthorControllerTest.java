package ru.otus.springframework.library.controllers.mvc;

import one.util.streamex.StreamEx;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@ActiveProfiles("mvc")
class AuthorControllerTest {

    @MockBean
    private AuthorService authorService;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mvc;

    @Test
    void all() throws Exception {
        var authors = List.of(mock(Author.class), mock(Author.class));
        when(authorService.all()).thenReturn(authors);

        var modelAndView = mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andReturn()
                .getModelAndView();

        verify(authorService).all();
        assertThat(modelAndView.getViewName(), equalTo("authors"));
        assertThat(modelAndView.getModel(), hasEntry("authors", authors));
    }

    @Test
    void addAuthor() throws Exception {
        var firstName = "fName";
        var lastName = "lName";
        var author = new Author(42L, firstName, lastName);
        when(authorService.newAuthor(firstName, lastName)).thenReturn(author);

        var modelAndView = mvc.perform(post("/author/add")
                .param("firstName", firstName)
                .param("lastName", lastName)
        ).andExpect(status().is3xxRedirection())
         .andReturn().getModelAndView();

        verify(authorService).newAuthor(firstName, lastName);
        assertThat(modelAndView.getViewName(), Matchers.equalTo("redirect:/author/" + author.getId()));
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void author(Long authorId, Optional<Author> author) throws Exception {
        when(authorService.withId(authorId)).thenReturn(author);
        var books = List.of(mock(Book.class));
        when(bookService.writtenBy(authorId)).thenReturn(books);

        var result = mvc.perform(get("/author/" + authorId));
        var modelAndView = result.andReturn().getModelAndView();

        verify(authorService).withId(authorId);
        if (author.isPresent()) {
            result.andExpect(status().isOk());
            assertThat(modelAndView.getViewName(), equalTo("author"));
            assertThat(modelAndView.getModel(), hasEntry("author", author.get()));
            assertThat(modelAndView.getModel(), hasEntry("books", books));
            verify(bookService).writtenBy(authorId);
        } else {
            result.andExpect(status().isNotFound());
        }
    }

    private static Stream<Arguments> authorProvider() {
        return StreamEx.of(
                Arguments.of(1L, Optional.of(mock(Author.class))),
                Arguments.of(42L, Optional.empty())
        );
    }

    @Test
    void delete() throws Exception {
        var authorId = 42L;
        var modelAndView = mvc.perform(post("/author/" + authorId + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();

        verify(authorService).removeAuthor(authorId);
        assertThat(modelAndView.getViewName(), Matchers.equalTo("redirect:/authors"));
    }
}