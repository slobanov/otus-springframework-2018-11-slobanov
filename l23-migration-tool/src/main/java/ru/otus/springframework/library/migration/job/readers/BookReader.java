package ru.otus.springframework.library.migration.job.readers;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.books.Book;

@Service
class BookReader extends GenericReader<Book> {
    BookReader(PagingAndSortingRepository<Book, Long> repository) {
        super(repository);
    }
}
