package ru.otus.springframework.library.dao.reactive;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.genres.Genre;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
public interface ReactiveBookMongodbRepository extends ReactiveBaseRepository<Book>, CustomSave<Book> {

    Flux<Book> findByAuthors(Mono<Author> author);
    Flux<Book> findByGenres(Mono<Genre> genre);
    Mono<Book> findByIsbn(String isbn);

    default Mono<Book> addAuthor(Book book, Mono<Author> author) {
        return author.map(a -> {
            book.getAuthors().add(a);
            return book;
        }).flatMap(this::save);
    }

    default Mono<Book> addGenre(Book book, Mono<Genre> genre) {
        return genre.map(g -> {
            book.getGenres().add(g);
            return book;
        }).flatMap(this::save);
    }

}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-reactive-mongodb-jpa")
class ReactiveBookMongodbRepositoryImpl implements CustomSave<Book> {

    private final ReactiveSequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private ReactiveBookMongodbRepository bookRepository;

    @Override
    public Mono<Book> saveObj(Book obj) {
        return sequenceRepository
                .getNextSequence("book")
                .flatMap(id -> {
                    obj.setId(id);
                    return bookRepository.save(obj);
                });
    }

}
