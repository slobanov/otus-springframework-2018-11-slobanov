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
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.genres.GenreService;

import java.util.Map;

@Controller
@Profile("mvc")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;
    private final BookService bookService;

    @GetMapping("/genres")
    public String all(Model model) {
        model.addAttribute("genres", genreService.all());
        return "genres";
    }

    @GetMapping("/genre/{name}")
    public String genre(@PathVariable String name, Model model) {
        model.addAllAttributes(Map.of(
                "genre", name,
                "books", bookService.ofGenre(name)
        ));
        return "genre";
    }

    @PostMapping("/genre/add")
    public ModelAndView addGenre(@RequestParam String genre) {
        genreService.newGenre(genre);
        return new ModelAndView("redirect:/genre/" + genre);
    }

    @PostMapping("/genre/{genre}/delete")
    public ModelAndView delete(@PathVariable String genre) {
        genreService.removeGenre(genre);
        return new ModelAndView("redirect:/genres");
    }
}
