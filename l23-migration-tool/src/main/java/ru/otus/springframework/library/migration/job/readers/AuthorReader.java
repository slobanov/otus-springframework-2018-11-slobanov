package ru.otus.springframework.library.migration.job.readers;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.authors.Author;

@Service
class AuthorReader extends GenericReader<Author> {
    AuthorReader(PagingAndSortingRepository<Author, Long> repository) {
        super(repository);
    }
}
