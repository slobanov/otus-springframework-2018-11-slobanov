package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-jpa")
interface AuthorDAOSpringJpa extends BaseDAOSpringJpa<Author>, AuthorDAO {}
