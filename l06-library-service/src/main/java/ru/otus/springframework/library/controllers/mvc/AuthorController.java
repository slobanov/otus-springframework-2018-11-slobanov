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

import java.util.Map;

@Controller
@Profile("mvc")
@RequiredArgsConstructor
@Slf4j
public class AuthorController {

    private final AuthorService authorService;
    private final BookService bookService;

    @GetMapping("/authors")
    public String all(Model model) {
        model.addAttribute("authors", authorService.all());
        return "authors";
    }

    @PostMapping("/author/add")
    public ModelAndView addAuthor(
            @RequestParam String firstName,
            @RequestParam String lastName
    ) {
        var author = authorService.newAuthor(firstName, lastName);
        return new ModelAndView("redirect:/author/" + author.getId());
    }

    @GetMapping("/author/{id}")
    public String author(@PathVariable Long id, Model model) {
        authorService.withId(id).map(author ->
            model.addAllAttributes(Map.of(
                    "author", author,
                    "books", bookService.writtenBy(id)
            ))
        ).orElseThrow(NotFoundException::new);
        return "author";
    }

    @PostMapping("/author/{authorId}/delete")
    public ModelAndView delete(@PathVariable Long authorId) {
        authorService.removeAuthor(authorId);
        return new ModelAndView("redirect:/authors");
    }
}
