package ru.otus.springframework.library.controllers.rest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Profile("rest")
public class AuthorController {

    @GetMapping("/authors")
    public String all() {
        return "authors";
    }

    @GetMapping("/author/{id}")
    public String author(@PathVariable Long id) {
        return "author";
    }

}
