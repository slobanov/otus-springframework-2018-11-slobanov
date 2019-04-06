package ru.otus.springframework.library.books.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.books.Book;

import java.util.List;

public interface BookServiceFlux {
    Flux<Book> all();
    Flux<Book> writtenBy(Long authorId);
    Flux<Book> ofGenre(String genre);

    Mono<Book> withIsbn(String isbn);

    Mono<Book> newBook(String isbn, String title, List<Long> authorsIds, List<String> genres);
    Mono<Book> removeBook(String isbn);

    Mono<Book> addAuthor(String isbn, Long authorId);
    Mono<Book> addGenre(String isbn, String genre);
}
