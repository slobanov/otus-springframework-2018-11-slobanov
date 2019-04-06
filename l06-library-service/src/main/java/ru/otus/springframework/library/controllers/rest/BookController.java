package ru.otus.springframework.library.controllers.rest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Profile({"rest", "flux"})
public class BookController {

    @GetMapping({"/books", "/"})
    public String all() {
        return "books";
    }

    @GetMapping("/book/{isbn}")
    public String book(@PathVariable String isbn) {
        return "book";
    }

}
