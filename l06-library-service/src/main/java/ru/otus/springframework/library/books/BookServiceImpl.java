package ru.otus.springframework.library.books;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.SimpleDAO;
import ru.otus.springframework.library.genres.Genre;

import java.util.*;

import static one.util.streamex.StreamEx.of;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Service
@Slf4j
class BookServiceImpl implements BookService {

    private final BookDAO bookDAO;
    private final SimpleDAO<Author> authorDAO;
    private final SimpleDAO<Genre> genreDAO;

    BookServiceImpl(BookDAO bookDAO, SimpleDAO<Author> authorDAO, SimpleDAO<Genre> genreDAO) {
        this.bookDAO = bookDAO;
        this.authorDAO = authorDAO;
        this.genreDAO = genreDAO;
    }

    @Override
    public List<Book> all() {
        return bookDAO.fetchAll();
    }

    @Override
    public List<Book> writtenBy(Long authorId) {
        log.debug("written by id: {}", authorId);

        var author = authorDAO.findById(authorId);
        log.debug("author: {}", author);

        return author.map(bookDAO::findByAuthor).orElse(Collections.emptyList());
    }

    @Override
    public List<Book> ofGenre(String genre) {
        var genreObj = asSingle(genreDAO.findByField("NAME", genre));
        return genreObj.isPresent() ? bookDAO.findByGenre(genreObj.get()) : Collections.emptyList();
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

        return bookDAO.save(new Book(
                isbn,
                title,
                authors,
                genreObjs
        ));
    }

    private List<Author> ensureAuthors(Collection<Long> authorsIds) {
        var authorsMap = of(authorsIds).mapToEntry(authorDAO::findById).toMap();

        var notFoundAuthors = EntryStream.of(authorsMap).filterValues(Optional::isEmpty).toMap();
        if (!notFoundAuthors.isEmpty()) {
            throw new IllegalArgumentException("Some authors don't exist: " +
                    of(notFoundAuthors.keySet())
                            .map(Objects::toString)
                            .joining(", ")
            );
        }

        return of(authorsMap.values()).flatMap(Optional::stream).toList();
    }

    private List<Genre> ensureGenres(Collection<String> genres) {
        var genreObjsMap = of(genres)
                .mapToEntry(g -> asSingle(genreDAO.findByField("NAME", g)))
                .toMap();

        var genresOfBook = of(genreObjsMap.values()).flatMap(Optional::stream);
        var notFoundGenres = EntryStream.of(genreObjsMap).filterValues(Optional::isEmpty).toMap();
        if (!notFoundGenres.isEmpty()) {
            log.debug("some genres don't exist: {}, creating...", notFoundGenres);
            genresOfBook = genresOfBook.append(
                    EntryStream.of(notFoundGenres).keys().map(g -> genreDAO.save(new Genre(g)))
            );
        }

        return genresOfBook.toList();
    }


    @Override
    public Optional<Book> removeBook(String isbn) {
        var book = withIsbn(isbn);
        return book.flatMap(bk -> bookDAO.deleteById(bk.getId()));
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
        var genreObj = asSingle(genreDAO.findByField("NAME", genre))
                .orElseGet(() -> genreDAO.save(new Genre(genre)));

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

}
