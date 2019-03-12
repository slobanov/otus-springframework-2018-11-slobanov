package ru.otus.springframework.library.dao.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;
import ru.otus.springframework.library.genres.Genre;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
interface BookDAOMongodb extends BaseDAOMongodb<Book>, BookDAO, CustomSave<Book> {

    @Override
    default Book addAuthor(Book book, Author author) {
        book.getAuthors().add(author);
        save(book);
        return book;
    }

    @Override
    default Book addGenre(Book book, Genre genre) {
        book.getGenres().add(genre);
        save(book);
        return book;
    }
}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
class BookDAOMongodbImpl implements CustomSave<Book> {

    private final SequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private BookDAOMongodb baseDAOMongodb;

    @Override
    public Book saveObj(Book obj) {
        obj.setId(sequenceRepository.getNextSequence("book"));
        return baseDAOMongodb.save(obj);
    }

}
