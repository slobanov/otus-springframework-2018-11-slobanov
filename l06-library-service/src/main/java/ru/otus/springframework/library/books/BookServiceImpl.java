package ru.otus.springframework.library.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.flux.BookServiceFlux;
import ru.otus.springframework.library.dao.AuthorDAO;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.*;

import static one.util.streamex.StreamEx.of;

@Service("bookService")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnMissingBean(BookServiceFlux.class)
class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;
    private final AuthorDAO authorDAO;
    private final GenreDAO genreDAO;

    @Override
    public List<Book> all() {
        return bookDAO.findAll();
    }

    @Override
    public List<Book> writtenBy(Long authorId) {
        log.debug("written by id: {}", authorId);

        var author = authorDAO.findById(authorId);
        log.debug("author: {}", author);

        return author.map(bookDAO::findByAuthors).orElse(Collections.emptyList());
    }

    @Override
    public List<Book> ofGenre(String genre) {
        var genreObj = genreDAO.findByName(genre);
        return genreObj.map(bookDAO::findByGenres).orElse(Collections.emptyList());
    }

    @Override
    public Optional<Book> withIsbn(String isbn) {
        return bookDAO.findByIsbn(isbn);
    }

    @Override
    @Transactional
    public Book newBook(String isbn, String title, List<Long> authorsIds, List<String> genres) {
        log.debug("new book: isbn {}; title {}; authorIds {}; genres {}",
                isbn, title, authorsIds, genres);

        if (withIsbn(isbn).isPresent()) {
            throw new IllegalArgumentException("Book with isbn " + isbn + " already exists");
        }

        var authors = ensureAuthors(authorsIds);
        log.debug("authors: {}", authors);
        var genreObjs = ensureGenres(genres);
        log.debug("genres: {}", genreObjs);

        return bookDAO.saveObj(new Book(
                isbn,
                title,
                authors,
                genreObjs
        ));
    }

    private Set<Author> ensureAuthors(Collection<Long> authorsIds) {
        var authorsMap = of(authorsIds).mapToEntry(authorDAO::findById).toMap();

        var notFoundAuthors = EntryStream.of(authorsMap).filterValues(Optional::isEmpty).toMap();
        if (!notFoundAuthors.isEmpty()) {
            throw new IllegalArgumentException("Some authors don't exist: " +
                    of(notFoundAuthors.keySet())
                            .map(Objects::toString)
                            .joining(", ")
            );
        }

        return of(authorsMap.values()).flatMap(Optional::stream).toSet();
    }

    private Set<Genre> ensureGenres(Collection<String> genres) {
        var genreObjsMap = of(genres)
                .mapToEntry(genreDAO::findByName)
                .toMap();

        var genresOfBook = of(genreObjsMap.values()).flatMap(Optional::stream);
        var notFoundGenres = EntryStream.of(genreObjsMap).filterValues(Optional::isEmpty).toMap();
        if (!notFoundGenres.isEmpty()) {
            log.debug("some genres don't exist: {}, creating...", notFoundGenres);
            genresOfBook = genresOfBook.append(
                    EntryStream.of(notFoundGenres).keys().map(g -> genreDAO.saveObj(new Genre(g)))
            );
        }

        return genresOfBook.toSet();
    }


    @Override
    public Optional<Book> removeBook(String isbn) {
        var book = withIsbn(isbn);
        return book.flatMap(bk -> bookDAO.deleteByObjId(bk.getId()));
    }

    @Override
    public Book addAuthor(String isbn, Long authorId) {
        var book = getBookByIsbn(isbn);
        var author = authorDAO.findById(authorId).orElseThrow(
                () -> new IllegalArgumentException("There is no author with id = " + authorId)
        );

        if (of(book.getAuthors()).findAny(a -> a.equals(author)).isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "Book %s already has an author %s", book, author
            ));
        }

        return bookDAO.addAuthor(book,author);
    }

    @Override
    @Transactional
    public Book addGenre(String isbn, String genre) {
        var book = getBookByIsbn(isbn);
        var genreObj = genreDAO.findByName(genre)
                .orElseGet(() -> genreDAO.saveObj(new Genre(genre)));

        if (of(book.getGenres()).findAny(g -> g.equals(genreObj)).isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "Book %s already has a genre %s", book, genre
            ));
        }

        return bookDAO.addGenre(book, genreObj);
    }

    private Book getBookByIsbn(String isbn) {
        return withIsbn(isbn).orElseThrow(
                () -> new IllegalArgumentException("There is no book with isbn: " + isbn)
        );
    }

    @Override
    public List<Genre> genresExceptBook(Book book) {
        return removeFromAll(genreDAO.findAll(), book.getGenres());
    }

    @Override
    public List<Author> authorsExceptBook(Book book) {
        return removeFromAll(authorDAO.findAll(), book.getAuthors());
    }

    private static <T> List<T> removeFromAll(List<T> list, Collection<T> set) {
        var newList = new ArrayList<>(list);
        newList.removeAll(set);
        return newList;
    }
}
