package ru.otus.springframework.library.books;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.SimpleDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static one.util.streamex.StreamEx.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookService bookService;

    private BookDAO bookDAO;
    private SimpleDAO<Author> authorDAO;
    private SimpleDAO<Genre> genreDAO;

    @BeforeEach
    void init() {
        bookDAO = mock(BookDAO.class);
        authorDAO = (SimpleDAO<Author>) mock(SimpleDAO.class);
        genreDAO = (SimpleDAO<Genre>) mock(SimpleDAO.class);

        bookService = new BookServiceImpl(bookDAO, authorDAO, genreDAO);
    }

    private static List<Book> someBooks() {
        return List.of(mock(Book.class), mock(Book.class));
    }

    @Test
    void all() {
        var books = someBooks();
        when(bookDAO.fetchAll()).thenReturn(books);

        assertThat(bookService.all(), equalTo(books));
    }

    @Test
    void writtenBy() {
        var books = someBooks();
        var author = new Author(1L, "a", "b");

        when(bookDAO.findByAuthor(author)).thenReturn(books);
        when(authorDAO.findById(1L)).thenReturn(Optional.of(author));

        assertThat(bookService.writtenBy(1L), equalTo(books));
    }

    @Test
    void writtenByNoAuthor() {
        when(bookDAO.findByAuthor(any(Author.class))).thenReturn(emptyList());
        when(authorDAO.findById(1L)).thenReturn(Optional.empty());

        assertThat(bookService.writtenBy(1L), equalTo(emptyList()));
    }

    @Test
    void ofGenre() {
        var books = someBooks();
        var genre = new Genre(1L, "genre");

        when(bookDAO.findByGenre(genre)).thenReturn(books);
        when(genreDAO.findByField("NAME", "genre")).thenReturn(List.of(genre));

        assertThat(bookService.ofGenre("genre"), equalTo(books));
    }

    @Test
    void ofGenreNoGenre() {
        var books = someBooks();

        when(bookDAO.findByGenre(any(Genre.class))).thenReturn(books);
        when(genreDAO.findByField(anyString(), anyString())).thenReturn(emptyList());

        assertThat(bookService.ofGenre("genre"), equalTo(emptyList()));
    }

    @Test
    void withIsbn() {
        var book = mock(Book.class);

        when(bookDAO.findByIsbn("1")).thenReturn(Optional.of(book));

        var result = bookService.withIsbn("1");
        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.get(), equalTo(book));
    }

    @Test
    void newBook() {
        var isbn = "isbn";
        var title = "title";

        var authorIds = List.of(1L, 42L);
        var authorMap = Map.of(
                1L, mock(Author.class),
                42L, mock(Author.class)
        );
        authorMap.forEach((id, a) -> when(authorDAO.findById(id)).thenReturn(Optional.of(a)));

        var genres = List.of("g1", "g2");
        var genreMap = Map.of(
                "g1", mock(Genre.class),
                "g2", mock(Genre.class)
        );
        genreMap.forEach((name, g) -> when(genreDAO.findByField("NAME", name)).thenReturn(List.of(g)));

        var book = new Book(
                isbn,
                title,
                of(authorMap.values()).toSet(),
                of(genreMap.values()).toSet(),
                Set.of()
        );

        when(bookService.withIsbn(isbn)).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenReturn(book);

        var resultBook = bookService.newBook(
                isbn,
                title,
                authorIds,
                genres
        );

        assertThat(resultBook, equalTo(book));
    }

    @Test
    void newBookAlreadyExists() {
        var isbn = "isbn";
        var book = mock(Book.class);
        when(bookService.withIsbn(isbn)).thenReturn(Optional.of(book));

        assertThrows(
                IllegalArgumentException.class,
                () -> bookService.newBook(isbn, "", List.of(), List.of())
        );
    }

    @Test
    void newBookNoAuthor() {
        var isbn = "isbn";
        var title = "title";

        var authorIds = List.of(1L, 42L);
        authorIds.forEach(id -> when(authorDAO.findById(id)).thenReturn(Optional.empty()));

        var genres = List.of("g1", "g2");
        var genreMap = Map.of(
                genres.get(0), mock(Genre.class),
                genres.get(1), mock(Genre.class)
        );
        genreMap.forEach((name, g) -> when(genreDAO.findByField("NAME", name)).thenReturn(List.of(g)));

        assertThrows(IllegalArgumentException.class,
                () -> bookService.newBook(
                        isbn,
                        title,
                        authorIds,
                        genres
                )
        );
        authorIds.forEach(id -> verify(authorDAO).findById(id));
    }

    @Test
    void newBookNoGenre() {
        var isbn = "isbn";
        var title = "title";

        var authorIds = List.of(1L, 42L);
        var authorMap = Map.of(
                1L, mock(Author.class),
                42L, mock(Author.class)
        );
        authorMap.forEach((id, a) -> when(authorDAO.findById(id)).thenReturn(Optional.of(a)));

        var genres = List.of("g1", "g2");
        var genreMap = Map.of(
                "g1", mock(Genre.class),
                "g2", mock(Genre.class)
        );
        genreMap.forEach((name, g) ->
                when(genreDAO.findByField("NAME", name))
                        .thenReturn("g1".equals(name) ? List.of(g) : List.of())
        );

        var book = new Book(
                isbn,
                title,
                of(authorMap.values()).toSet(),
                of(genreMap.values()).toSet(),
                Set.of()
        );

        when(bookService.withIsbn(isbn)).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenReturn(book);

        var resultBook = bookService.newBook(
                isbn,
                title,
                authorIds,
                genres
        );

        verify(genreDAO).save(new Genre("g2"));
        assertThat(resultBook, equalTo(book));
    }

    @Test
    void removeBook() {
        var book = mock(Book.class);

        when(bookDAO.findByIsbn("123")).thenReturn(Optional.of(book));
        when(bookDAO.deleteById(anyLong())).thenReturn(Optional.of(book));

        var result = bookService.removeBook("123");
        assertThat(result.isPresent(), equalTo(true));
        assertThat(result.get(), equalTo(book));
    }

    @Test
    void removeBookNoBook() {
        when(bookDAO.findByIsbn("123")).thenReturn(Optional.empty());

        var result = bookService.removeBook("123");
        assertThat(result.isPresent(), equalTo(false));
    }

    @Test
    void addAuthor() {
        var bookId = 1L;
        var bookIsbn = "isbn";
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of(), Set.of());
        var authorId = 2L;
        var author = new Author(authorId, "fName", "lName");

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(bookDAO.addAuthor(book, author)).thenReturn(book);
        when(authorDAO.findById(authorId)).thenReturn(Optional.of(author));

        var newBook = bookService.addAuthor(bookIsbn, authorId);
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO).addAuthor(book, author);
        verify(authorDAO).findById(authorId);
        assertThat(newBook, equalTo(book));
    }

    @Test
    void addAuthorNoAuthor() {
        var bookId = 1L;
        var bookIsbn = "isbn";
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of(), Set.of());
        var authorId = 2L;

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(authorDAO.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.addAuthor(bookIsbn, authorId));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(authorDAO).findById(authorId);
    }

    @Test
    void addAuthorDuplicate() {
        var bookId = 1L;
        var bookIsbn = "isbn";
        var authorId = 2L;
        var author = new Author(authorId, "fName", "lName");
        var book = new Book(bookId, bookIsbn, "title", Set.of(author), Set.of(), Set.of());

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(authorDAO.findById(authorId)).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class, () -> bookService.addAuthor(bookIsbn, authorId));

        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO, never()).addAuthor(book, author);
        verify(authorDAO).findById(authorId);
    }

    @Test
    void addAuthorNoBook() {
        var bookIsbn = "isbn";
        var authorId = 2L;

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookService.addAuthor(bookIsbn, authorId));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(authorDAO, never()).findById(authorId);
    }

    @Test
    void addGenre() {
        var bookIsbn = "isbn";
        var bookId = 1L;
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of(), Set.of());
        var genre = "genre";
        var genreObj = new Genre(2L, genre);

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(bookDAO.addGenre(book, genreObj)).thenReturn(book);
        when(genreDAO.findByField("NAME", genre)).thenReturn(List.of(genreObj));

        var newBook = bookService.addGenre(bookIsbn, genre);

        assertThat(newBook, equalTo(book));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO).addGenre(book, genreObj);
        verify(genreDAO, never()).save(genreObj);
    }

    @Test
    void addGenreDuplicate() {
        var bookIsbn = "isbn";
        var bookId = 1L;
        var genre = "genre";
        var genreObj = new Genre(2L, genre);
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of(genreObj), Set.of());

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(genreDAO.findByField("NAME", genre)).thenReturn(List.of(genreObj));

        assertThrows(IllegalArgumentException.class, () -> bookService.addGenre(bookIsbn, genre));

        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO, never()).addGenre(book, genreObj);
        verify(genreDAO, never()).save(genreObj);
    }

    @Test
    void addGenreNoBook() {
        var bookIsbn = "isbn";
        var genre = "genre";

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.addGenre(bookIsbn, genre));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(genreDAO, never()).findByField("NAME", genre);
    }
}