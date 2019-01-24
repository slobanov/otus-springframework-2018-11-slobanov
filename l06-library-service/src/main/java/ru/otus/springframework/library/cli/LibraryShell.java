package ru.otus.springframework.library.cli;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.Table;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.cli.presenters.PresenterService;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.genres.GenreService;

import javax.validation.constraints.NotBlank;

import static java.util.Arrays.asList;

@ShellComponent
@RequiredArgsConstructor
class LibraryShell {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    private final PresenterService presenterService;

    @ShellMethod("List all books in the library.")
    Table allBooks() {
        return presenterService.present(
                bookService.all(),
                Book.class
        );
    }

    @ShellMethod("List all books written by given author.")
    Table authorBooks(@ShellOption({"-a", "--authorId"}) Long authorId) {
        return presenterService.present(
                bookService.writtenBy(authorId),
                Book.class
        );
    }

    @ShellMethod("List all books of a particular genre.")
    Table booksOfGenre(@ShellOption({"-g", "--genre"}) String genre) {
        return presenterService.present(
                bookService.ofGenre(genre),
                Book.class
        );
    }

    @ShellMethod("List all available authors.")
    Table allAuthors() {
        return presenterService.present(
                authorService.all(),
                Author.class
        );
    }

    @ShellMethod("List all available genres.")
    Table allGenres() {
        return presenterService.present(
                genreService.all(),
                Genre.class
        );
    }

    @ShellMethod("Add new author.")
    Table addAuthor(
            @ShellOption({"-f", "--first-name"})@NotBlank String firstName,
            @ShellOption({"-l", "--last-name"})@NotBlank String lastName
    ) {
        return presenterService.present(authorService.newAuthor(firstName, lastName), Author.class);
    }

    @ShellMethod("Add new genre.")
    Table addGenre(
            @ShellOption({"-g", "--genre"})@NotBlank String name
    ) {
        return presenterService.present(genreService.newGenre(name), Genre.class);
    }

    @ShellMethod("Add new book.")
    Table addBook(
         @ShellOption({"-i", "--isbn"}) String isbn,
         @ShellOption({"-t", "--title"}) String title,
         @ShellOption({"-a", "--authorIds"}) Long[] authorIds,
         @ShellOption({"-g", "--genres"}) String[] genres
    ) {
        return presenterService.present(bookService.newBook(
                isbn,
                title,
                asList(authorIds),
                asList(genres)
        ), Book.class);
    }

    @ShellMethod("Remove genre.")
    Table removeGenre(@ShellOption({"-g", "--genre"}) String name) {
        return presenterService.present(genreService.removeGenre(name), Genre.class);
    }

    @ShellMethod("Remove genre.")
    Table removeAuthor(@ShellOption({"-a", "--authorId"}) Long authorId) {
        return presenterService.present(authorService.removeAuthor(authorId), Author.class);
    }

    @ShellMethod("Remove book.")
    Table removeBook(@ShellOption({"-i", "--isbn"}) String isbn) {
        return presenterService.present(bookService.removeBook(isbn), Book.class);
    }

    @ShellMethod("Add genre to book")
    Table addGenreToBook(
            @ShellOption({"-i", "--isbn"}) String isbn,
            @ShellOption({"-g", "--genre"}) String genre
    ) {
        return presenterService.present(bookService.addGenre(isbn, genre), Book.class);
    }

    @ShellMethod("Add author to book")
    Table addAuthorToBook(
            @ShellOption({"-i", "--isbn"}) String isbn,
            @ShellOption({"-a", "--authorId"}) Long authorId
    ) {
        return presenterService.present(bookService.addAuthor(isbn, authorId), Book.class);
    }

}
