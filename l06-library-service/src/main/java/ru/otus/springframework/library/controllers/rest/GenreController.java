package ru.otus.springframework.library.controllers.rest;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Profile("rest")
public class GenreController {

    @GetMapping("/genres")
    public String all() {
        return "genres";
    }

    @GetMapping("/genre/{name}")
    public String genre(@PathVariable String name) {
        return "genre";
    }

}
