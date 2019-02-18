package ru.otus.springframework.library.dao;

import ru.otus.springframework.library.genres.Genre;

import java.util.Optional;

public interface GenreDAO extends SimpleDAO<Genre> {
    Optional<Genre> findByName(String name);
}
