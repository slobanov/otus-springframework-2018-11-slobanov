package ru.otus.springframework.library.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;

@Repository
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {}
