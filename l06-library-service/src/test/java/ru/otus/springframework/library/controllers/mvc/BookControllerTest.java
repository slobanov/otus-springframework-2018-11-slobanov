package ru.otus.springframework.library.controllers.mvc;

import one.util.streamex.StreamEx;
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
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles({"mvc", "test-mongodb"})
class BookControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "test_user", password = "test_password")
    void all() throws Exception {
        var books = List.of(mock(Book.class), mock(Book.class));
        when(bookService.all()).thenReturn(books);

        var authors = List.of(mock(Author.class), mock(Author.class));
        when(authorService.all()).thenReturn(authors);

        var genres = List.of(mock(Genre.class), mock(Genre.class));
        when(genreService.all()).thenReturn(genres);

        var modelAndView = mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getModelAndView();

        verify(bookService).all();

        assertThat(modelAndView.getViewName(), equalTo("books"));
        assertThat(modelAndView.getModel(), hasEntry("books", books));
        assertThat(modelAndView.getModel(), hasEntry("authors", authors));
        assertThat(modelAndView.getModel(), hasEntry("genres", genres));
    }

    @Test
    void all302() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @ParameterizedTest
    @MethodSource("bookProvider")
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void book(Optional<Book> book, String isbn) throws Exception {
        when(bookService.withIsbn(isbn)).thenReturn(book);

        var result = mvc.perform(get("/book/" + isbn));
        var modelAndView = result.andReturn().getModelAndView();

        verify(bookService).withIsbn(isbn);
        if (book.isPresent()) {
            result.andExpect(status().isOk());
            assertThat(modelAndView.getViewName(), equalTo("book"));
            assertThat(modelAndView.getModel(), hasEntry("book", book.get()));
            assertThat(modelAndView.getModel(), hasKey("otherAuthors"));
            assertThat(modelAndView.getModel(), hasKey("otherGenres"));
            assertThat(modelAndView.getModel(), hasKey("comments"));

            verify(commentService).commentsFor(isbn);
        } else {
            result.andExpect(status().isNotFound());
        }
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "OTHER_ROLE")
    void book403() throws Exception {
        mvc.perform(get("/book/123")).andExpect(status().isForbidden());
    }

    @Test
    void book302() throws Exception {
        mvc.perform(get("/book/123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    private static Stream<Arguments> bookProvider() {
        return StreamEx.of(
                Arguments.of(Optional.empty(), "123"),
                Arguments.of(Optional.of(mock(Book.class)), "456")
        );
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addBook() throws Exception {
        var isbn = "123";
        var title = "title1";
        var modelAndView = mvc.perform(post("/book/add")
                .param("isbn", isbn)
                .param("title", title)
                .param("authorIds", "1", "2")
                .param("genres", "g1", "g2")
        ).andExpect(status().is3xxRedirection())
         .andReturn().getModelAndView();

        verify(bookService).newBook(isbn, title, List.of(1L, 2L), List.of("g1", "g2"));
        assertThat(modelAndView.getViewName(), equalTo("redirect:/book/" + isbn));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addBook403() throws Exception {
        var isbn = "123";
        var title = "title1";
        mvc.perform(post("/book/add")
                .param("isbn", isbn)
                .param("title", title)
                .param("authorIds", "1", "2")
                .param("genres", "g1", "g2")
        ).andExpect(status().isForbidden());
    }

    @Test
    void addBook302() throws Exception {
        var isbn = "123";
        var title = "title1";
        mvc.perform(post("/book/add")
                .param("isbn", isbn)
                .param("title", title)
                .param("authorIds", "1", "2")
                .param("genres", "g1", "g2")
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void delete() throws Exception {
        var isbn = "123";
        var modelAndView = mvc.perform(post("/book/" + isbn + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();

        verify(bookService).removeBook(isbn);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/books"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void delete403() throws Exception {
        var isbn = "123";
        mvc.perform(post("/book/" + isbn + "/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete302() throws Exception {
        var isbn = "123";
        mvc.perform(post("/book/" + isbn + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addAuthor() throws Exception {
        var isbn = "123";
        var authorId = "1";

        var modelAndView = mvc.perform(post("/book/" + isbn + "/addAuthor")
                .param("authorId", authorId)
        ).andExpect(status().is3xxRedirection())
         .andReturn().getModelAndView();

        verify(bookService).addAuthor(isbn, Long.valueOf(authorId));
        assertThat(modelAndView.getViewName(), equalTo("redirect:/book/" + isbn));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addAuthor403() throws Exception {
        var isbn = "123";
        var authorId = "1";

       mvc.perform(post("/book/" + isbn + "/addAuthor")
                .param("authorId", authorId)
        ).andExpect(status().isForbidden());
    }

    @Test
    void addAuthor302() throws Exception {
        var isbn = "123";
        var authorId = "1";

        mvc.perform(post("/book/" + isbn + "/addAuthor")
                .param("authorId", authorId)
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("http://localhost/login.html"));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addGenre() throws Exception {
        var isbn = "123";
        var genre = "genre";

        var modelAndView = mvc.perform(post("/book/" + isbn + "/addGenre")
                .param("genre", genre)
        ).andExpect(status().is3xxRedirection())
         .andReturn().getModelAndView();

        verify(bookService).addGenre(isbn, genre);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/book/" + isbn));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addGenre403() throws Exception {
        var isbn = "123";
        var genre = "genre";

        mvc.perform(post("/book/" + isbn + "/addGenre")
                .param("genre", genre)
        ).andExpect(status().isForbidden());
    }

    @Test
    void addGenre302() throws Exception {
        var isbn = "123";
        var genre = "genre";

        mvc.perform(post("/book/" + isbn + "/addGenre")
                .param("genre", genre)
        ).andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void addComment() throws Exception {
        var isbn = "123";
        var comment = "comment";

        var modelAndView = mvc.perform(post("/book/" + isbn + "/addComment")
                .param("comment", comment)
        ).andExpect(status().is3xxRedirection())
         .andReturn().getModelAndView();

        verify(commentService).newComment(isbn, comment);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/book/" + isbn));
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void addComment403() throws Exception {
        var isbn = "123";
        var comment = "comment";

        mvc.perform(post("/book/" + isbn + "/addComment")
                .param("comment", comment)
        ).andExpect(status().isForbidden());
    }

    @Test
    void addComment302() throws Exception {
        var isbn = "123";
        var comment = "comment";

        mvc.perform(post("/book/" + isbn + "/addComment")
                .param("comment", comment)
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrl("http://localhost/login.html"));

    }
}