package ru.otus.springframework.library.books;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyList;
import static one.util.streamex.StreamEx.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookService bookService;

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private GenreDAO genreDAO;

    @BeforeEach
    void init() {
        bookDAO = mock(BookDAO.class);
        authorDAO = mock(AuthorDAO.class);
        genreDAO = mock(GenreDAO.class);

        bookService = new BookServiceImpl(bookDAO, authorDAO, genreDAO);
    }

    private static List<Book> someBooks() {
        return List.of(mock(Book.class), mock(Book.class));
    }

    @Test
    void all() {
        var books = someBooks();
        when(bookDAO.findAll()).thenReturn(books);

        assertThat(bookService.all(), equalTo(books));
    }

    @Test
    void writtenBy() {
        var books = someBooks();
        var author = new Author(1L, "a", "b");

        when(bookDAO.findByAuthors(author)).thenReturn(books);
        when(authorDAO.findById(1L)).thenReturn(Optional.of(author));

        assertThat(bookService.writtenBy(1L), equalTo(books));
    }

    @Test
    void writtenByNoAuthor() {
        when(bookDAO.findByAuthors(any(Author.class))).thenReturn(emptyList());
        when(authorDAO.findById(1L)).thenReturn(Optional.empty());

        assertThat(bookService.writtenBy(1L), equalTo(emptyList()));
    }

    @Test
    void ofGenre() {
        var books = someBooks();
        var genre = new Genre(1L, "genre");

        when(bookDAO.findByGenres(genre)).thenReturn(books);
        when(genreDAO.findByName("genre")).thenReturn(Optional.of(genre));

        assertThat(bookService.ofGenre("genre"), equalTo(books));
    }

    @Test
    void ofGenreNoGenre() {
        var books = someBooks();

        when(bookDAO.findByGenres(any(Genre.class))).thenReturn(books);
        when(genreDAO.findByName(anyString())).thenReturn(Optional.empty());

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
        genreMap.forEach((name, g) -> when(genreDAO.findByName(name)).thenReturn(Optional.of(g)));

        var book = new Book(
                isbn,
                title,
                of(authorMap.values()).toSet(),
                of(genreMap.values()).toSet()
        );

        when(bookService.withIsbn(isbn)).thenReturn(Optional.empty());
        when(bookDAO.saveObj(any(Book.class))).thenReturn(book);

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
        genreMap.forEach((name, g) -> when(genreDAO.findByName(name)).thenReturn(Optional.of(g)));

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
                when(genreDAO.findByName(name))
                        .thenReturn("g1".equals(name) ? Optional.of(g) : Optional.empty())
        );

        var book = new Book(
                isbn,
                title,
                of(authorMap.values()).toSet(),
                of(genreMap.values()).toSet()
        );

        when(bookService.withIsbn(isbn)).thenReturn(Optional.empty());
        when(bookDAO.saveObj(any(Book.class))).thenReturn(book);

        var resultBook = bookService.newBook(
                isbn,
                title,
                authorIds,
                genres
        );

        verify(genreDAO).saveObj(new Genre("g2"));
        assertThat(resultBook, equalTo(book));
    }

    @Test
    void removeBook() {
        var book = mock(Book.class);

        when(bookDAO.findByIsbn("123")).thenReturn(Optional.of(book));
        when(bookDAO.deleteByObjId(anyLong())).thenReturn(Optional.of(book));

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
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of());
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
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of());
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
        var book = new Book(bookId, bookIsbn, "title", Set.of(author), Set.of());

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
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of());
        var genre = "genre";
        var genreObj = new Genre(2L, genre);

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(bookDAO.addGenre(book, genreObj)).thenReturn(book);
        when(genreDAO.findByName(genre)).thenReturn(Optional.of(genreObj));

        var newBook = bookService.addGenre(bookIsbn, genre);

        assertThat(newBook, equalTo(book));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO).addGenre(book, genreObj);
        verify(genreDAO, never()).saveObj(genreObj);
    }

    @Test
    void addGenreDuplicate() {
        var bookIsbn = "isbn";
        var bookId = 1L;
        var genre = "genre";
        var genreObj = new Genre(2L, genre);
        var book = new Book(bookId, bookIsbn, "title", Set.of(), Set.of(genreObj));

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.of(book));
        when(genreDAO.findByName(genre)).thenReturn(Optional.of(genreObj));

        assertThrows(IllegalArgumentException.class, () -> bookService.addGenre(bookIsbn, genre));

        verify(bookDAO).findByIsbn(bookIsbn);
        verify(bookDAO, never()).addGenre(book, genreObj);
        verify(genreDAO, never()).saveObj(genreObj);
    }

    @Test
    void addGenreNoBook() {
        var bookIsbn = "isbn";
        var genre = "genre";

        when(bookDAO.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookService.addGenre(bookIsbn, genre));
        verify(bookDAO).findByIsbn(bookIsbn);
        verify(genreDAO, never()).findByName(genre);
    }

    @Test
    void genresExceptBookTest() {
        var g1 = mock(Genre.class);
        var g2 = mock(Genre.class);
        var g3 = mock(Genre.class);

        var genres = List.of(g1, g2);
        when(genreDAO.findAll()).thenReturn(genres);

        var book = new Book("123", "title", Set.of(), Set.of(g1, g3));

        var resGenres = bookService.genresExceptBook(book);
        assertThat(resGenres, hasSize(1));
        assertThat(resGenres, equalTo(List.of(g2)));
    }

    @Test
    void authorsExceptBookTest() {
        var a1 = mock(Author.class);
        var a2 = mock(Author.class);
        var a3 = mock(Author.class);

        var authors = List.of(a1, a2, a3);
        when(authorDAO.findAll()).thenReturn(authors);

        var book = new Book("123", "title", Set.of(a1, a3), Set.of());

        var resAuthors = bookService.authorsExceptBook(book);
        assertThat(resAuthors, hasSize(1));
        assertThat(resAuthors, equalTo(List.of(a2)));
    }
}