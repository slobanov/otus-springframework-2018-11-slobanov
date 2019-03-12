package ru.otus.springframework.library.controllers.mvc;

import one.util.streamex.StreamEx;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@ActiveProfiles("mvc")
class BookControllerTest {

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

    @ParameterizedTest
    @MethodSource("bookProvider")
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

    private static Stream<Arguments> bookProvider() {
        return StreamEx.of(
                Arguments.of(Optional.empty(), "123"),
                Arguments.of(Optional.of(mock(Book.class)), "456")
        );
    }

    @Test
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
    void delete() throws Exception {
        var isbn = "123";
        var modelAndView = mvc.perform(post("/book/" + isbn + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getModelAndView();

        verify(bookService).removeBook(isbn);
        assertThat(modelAndView.getViewName(), equalTo("redirect:/books"));
    }

    @Test
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
}