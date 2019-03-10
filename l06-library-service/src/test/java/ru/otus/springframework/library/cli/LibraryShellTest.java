package ru.otus.springframework.library.cli;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.shell.Shell;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.cli.presenters.PresenterService;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles({"shell", "test", "test-jpa"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LibraryShellTest {

    @Autowired
    private Shell shell;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private PresenterService presenterService;

    private static List<Book> someBooks() {
        return List.of(mock(Book.class));
    }

    @Test
    void allBooks() {
        var books = someBooks();
        when(bookService.all()).thenReturn(books);
        shell.evaluate(() -> "all-books");
        verify(bookService).all();
        verify(presenterService).present(books, Book.class);
    }

    @Test
    void authorBooks() {
        var books = someBooks();
        var aId = 1L;
        when(bookService.writtenBy(aId)).thenReturn(books);
        shell.evaluate(() -> "author-books --authorId " + aId);
        verify(bookService).writtenBy(aId);
        verify(presenterService).present(books, Book.class);
    }

    @Test
    void booksOfGenre() {
        var books = someBooks();
        var genre = "qwe";
        when(bookService.ofGenre(genre)).thenReturn(books);
        shell.evaluate(() -> "books-of-genre --genre " + genre);
        verify(bookService).ofGenre(genre);
        verify(presenterService).present(books, Book.class);
    }

    @Test
    void allAuthors() {
        var authors = List.of(mock(Author.class));
        when(authorService.all()).thenReturn(authors);
        shell.evaluate(() -> "all-authors");
        verify(authorService).all();
        verify(presenterService).present(authors, Author.class);
    }

    @Test
    void allGenres() {
        var genres = List.of(mock(Genre.class));
        when(genreService.all()).thenReturn(genres);
        shell.evaluate(() -> "all-genres");
        verify(genreService).all();
        verify(presenterService).present(genres, Genre.class);
    }

    @Test
    void addAuthor() {
        var fName = "fName";
        var lName = "lName";
        var author = new Author(fName, lName);

        when(authorService.newAuthor(fName, lName)).thenReturn(author);
        shell.evaluate(() -> format("add-author -f %s -l %s", fName, lName));
        verify(authorService).newAuthor(fName, lName);
        verify(presenterService).present(author, Author.class);
    }

    @Test
    void addGenre() {
        var genre = "genre";
        var genreObj = new Genre(genre);
        when(genreService.newGenre(genre)).thenReturn(genreObj);
        shell.evaluate(() -> "add-genre -g " + genre);
        verify(genreService).newGenre(genre);
        verify(presenterService).present(genreObj, Genre.class);
    }

    @Test
    void addBook() {
        var isbn = "isbn";
        var title = "title";
        var authorsId = List.of(1L, 2L);
        var genres = List.of("genre");
        var book = mock(Book.class);
        when(bookService.newBook(isbn, title, authorsId, genres)).thenReturn(book);
        shell.evaluate(() -> "add-book -a 1,2 -g genre -i isbn -t title");
        verify(bookService).newBook(isbn, title, authorsId, genres);
        verify(presenterService).present(book, Book.class);
    }

    @Test
    void removeGenre() {
        var genre = "genre";
        var genreObj = new Genre(genre);
        when(genreService.removeGenre(genre)).thenReturn(Optional.of(genreObj));
        shell.evaluate(() -> "remove-genre -g " + genre);
        verify(genreService).removeGenre(genre);
        verify(presenterService).present(Optional.of(genreObj), Genre.class);
    }

    @Test
    void removeAuthor() {
        var aId = 42L;
        var author = mock(Author.class);

        when(authorService.removeAuthor(42L)).thenReturn(Optional.of(author));
        shell.evaluate(() -> "remove-author -a " + aId);
        verify(authorService).removeAuthor(aId);
        verify(presenterService).present(Optional.of(author), Author.class);
    }

    @Test
    void removeBook() {
        var isbn = "isbn";
        var book = mock(Book.class);
        when(bookService.removeBook(isbn)).thenReturn(Optional.of(book));
        shell.evaluate(() -> "remove-book -i " + isbn);
        verify(bookService).removeBook(isbn);
        verify(presenterService).present(Optional.of(book), Book.class);
    }

    @Test
    void addGenreToBook() {
        var book = mock(Book.class);
        var isbn = "isbn";
        var genre = "genre";
        when(bookService.addGenre(isbn, genre)).thenReturn(book);
        shell.evaluate(() -> format("add-genre-to-book -i %s -g %s", isbn, genre));
        verify(bookService).addGenre(isbn, genre);
        verify(presenterService).present(book, Book.class);
    }

    @Test
    void addAuthorToBook() {
        var book = mock(Book.class);
        var isbn = "isbn";
        var aId = 42L;
        when(bookService.addAuthor(isbn, aId)).thenReturn(book);
        shell.evaluate(() -> format("add-author-to-book -i %s -a %s", isbn, aId));
        verify(bookService).addAuthor(isbn, aId);
        verify(presenterService).present(book, Book.class);
    }

    @Test
    void allComments() {
        var isbn = "isbn";
        var comments = List.of(mock(Comment.class));
        when(commentService.commentsFor(isbn)).thenReturn(comments);
        shell.evaluate(() -> "all-comments -i " + isbn);
        verify(commentService).commentsFor(isbn);
        verify(presenterService).present(comments, Comment.class);
    }

    @Test
    void addCommentToBook() {
        var isbn = "isbn";
        var text = "text";
        var comment = mock(Comment.class);

        when(commentService.newComment(isbn, text)).thenReturn(comment);
        shell.evaluate(() -> format("add-comment -i %s -t %s", isbn, text));
        verify(commentService).newComment(isbn, text);
        verify(presenterService).present(comment, Comment.class);
    }
}