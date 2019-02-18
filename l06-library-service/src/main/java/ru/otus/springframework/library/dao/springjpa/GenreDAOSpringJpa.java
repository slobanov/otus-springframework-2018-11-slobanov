package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.Genre;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface GenreDAOSpringJpa extends BaseDAOSpringJpa<Genre>, GenreDAO {}
