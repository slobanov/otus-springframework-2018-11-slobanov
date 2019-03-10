package ru.otus.springframework.library.controllers.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;
import java.util.Map;

@Controller
@Profile("mvc")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;
    private final AuthorService authorService;
    private final GenreService genreService;

    private static final String REDIRECT_BOOKS = "redirect:/books";
    private static final String REDIRECT_BOOK = "redirect:/book/";

    @GetMapping({"/books", "/"})
    public String all(Model model) {
        model.addAttribute("books", bookService.all());
        model.addAttribute("authors", authorService.all());
        model.addAttribute("genres", genreService.all());
        return "books";
    }

    @GetMapping("/book/{isbn}")
    public String book(@PathVariable String isbn, Model model) {
        bookService.withIsbn(isbn)
                .map(book -> model.addAllAttributes(Map.of(
                        "book", book,
                        "comments", commentService.commentsFor(isbn),
                        "otherGenres", bookService.genresExceptBook(book),
                        "otherAuthors", bookService.authorsExceptBook(book)
                ))
                ).orElseThrow(NotFoundException::new);
        return "book";
    }

    @PostMapping("/book/add")
    public ModelAndView addBook(
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam List<Long> authorIds,
            @RequestParam List<String> genres
    ) {
        bookService.newBook(isbn, title, authorIds, genres);
        return new ModelAndView(REDIRECT_BOOK + isbn);
    }

    @PostMapping("/book/{isbn}/delete")
    public ModelAndView delete(@PathVariable String isbn) {
        bookService.removeBook(isbn);
        return new ModelAndView(REDIRECT_BOOKS);
    }

    @PostMapping("/book/{isbn}/addAuthor")
    public ModelAndView addAuthor(@PathVariable String isbn, @RequestParam Long authorId) {
        bookService.addAuthor(isbn, authorId);
        return new ModelAndView(REDIRECT_BOOK + isbn);
    }

    @PostMapping("/book/{isbn}/addGenre")
    public ModelAndView addGenre(@PathVariable String isbn, @RequestParam String genre) {
        bookService.addGenre(isbn, genre);
        return new ModelAndView(REDIRECT_BOOK + isbn);
    }

    @PostMapping("/book/{isbn}/addComment")
    public ModelAndView addComment(@PathVariable String isbn, @RequestParam String comment) {
        commentService.newComment(isbn, comment);
        return new ModelAndView(REDIRECT_BOOK + isbn);
    }

}
