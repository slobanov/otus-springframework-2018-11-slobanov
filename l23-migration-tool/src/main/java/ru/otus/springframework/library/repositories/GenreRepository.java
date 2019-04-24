package ru.otus.springframework.library.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.genres.Genre;

@Repository
public interface GenreRepository extends PagingAndSortingRepository<Genre, Long> {}
