package ru.otus.springframework.library.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.books.Book;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {}

