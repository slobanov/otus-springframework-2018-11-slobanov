package ru.otus.springframework.library.genres;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<Genre> all();
    Genre newGenre(String name);
    Optional<Genre> removeGenre(String name);
}
