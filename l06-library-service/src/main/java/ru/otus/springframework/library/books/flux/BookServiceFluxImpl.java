package ru.otus.springframework.library.books.flux;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.reactive.ReactiveAuthorMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveBookMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveGenreMongodbRepository;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;

import static one.util.streamex.StreamEx.of;
import static reactor.core.publisher.Mono.error;

@Component
@Profile("flux")
@RequiredArgsConstructor
@Slf4j
public class BookServiceFluxImpl implements BookServiceFlux {

    private final ReactiveBookMongodbRepository bookRepository;
    private final ReactiveAuthorMongodbRepository authorRepository;
    private final ReactiveGenreMongodbRepository genreRepository;

    @Override
    public Flux<Book> all() {
        log.info("find all");
        return bookRepository.findAll()
                .doOnNext(book -> log.debug("find all; book {}", book));
    }

    @Override
    public Flux<Book> writtenBy(Long authorId) {
        log.info("find by authorId: {}", authorId);
        return bookRepository.findByAuthors(authorRepository.findById(authorId))
                .doOnNext(book -> log.debug("find by author id {}; book {}", authorId, book));
    }

    @Override
    public Flux<Book> ofGenre(String genre) {
        log.info("find by genre: {}", genre);
        return bookRepository.findByGenres(genreRepository.findByName(genre))
                .doOnNext(book -> log.debug("find by genre {}; book {}", genre, book));
    }

    @Override
    public Mono<Book> withIsbn(String isbn) {
        log.info("with isbn: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .doOnNext(book -> log.debug("with isbn {}; book {}", isbn, book));
    }

    @Override
    public Mono<Book> newBook(String isbn, String title, List<Long> authorsIds, List<String> genres) {
        log.info("new book; isbn {}; title {}; authorsIds: {}, genres {}",
                isbn, title, authorsIds, genres);

        var authors = Flux.fromIterable(authorsIds)
                .flatMap(aId -> authorRepository.findById(aId).switchIfEmpty(
                        error(() -> new IllegalArgumentException("there is no author with id = " + aId)))
                ).collectList();

        var genreObjs = Flux.fromIterable(genres)
                .flatMap(genre -> genreRepository.findByName(genre).switchIfEmpty(
                        genreRepository.saveObj(new Genre(genre))
                )).collectList();

        return withIsbn(isbn)
                .flatMap(book -> Mono.<Book>error(() ->
                    new IllegalArgumentException("Book with isbn " + isbn + " already exists")
                )).switchIfEmpty(
                        authors.flatMap(as -> genreObjs.map(
                                gs -> new Book(isbn, title, of(as).toSet(), of(gs).toSet())
                        ))
                ).flatMap(bookRepository::saveObj);
    }

    @Override
    public Mono<Book> removeBook(String isbn) {
        log.info("delete isbn: {}", isbn);
        return withIsbn(isbn).flatMap(book -> bookRepository.deleteByObjId(book.getId()))
                .doOnNext(book -> log.debug("delete isbn: {}; book {}", isbn, book));
    }

    @Override
    public Mono<Book> addAuthor(String isbn, Long authorId) {
        var author = authorRepository.findById(authorId).switchIfEmpty(
                error(() ->  new IllegalArgumentException("There is no author with id = " + authorId)
        ));

        return getBookByIsbn(isbn).flatMap(book -> bookRepository.addAuthor(book, author));
    }

    @Override
    public Mono<Book> addGenre(String isbn, String genre) {
        var genreObj = genreRepository.findByName(genre)
                .switchIfEmpty(genreRepository.saveObj(new Genre(genre)));

        return getBookByIsbn(isbn).flatMap(book -> bookRepository.addGenre(book, genreObj));
    }

    private Mono<Book> getBookByIsbn(String isbn) {
        return withIsbn(isbn).switchIfEmpty(
                error(() -> new IllegalArgumentException("There is no book with isbn: " + isbn))
        );
    }
}
