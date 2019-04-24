package ru.otus.springframework.library.migration.job.readers;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.genres.Genre;

@Service
class GenreReader extends GenericReader<Genre> {
    GenreReader(PagingAndSortingRepository<Genre, Long> repository) {
        super(repository);
    }
}
