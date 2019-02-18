package ru.otus.springframework.library.dao.mongodb.mongock.changelog;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.mongodb.BookComments;
import ru.otus.springframework.library.dao.mongodb.MongoComment;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;
import ru.otus.springframework.library.genres.Genre;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.builder;
import static java.lang.Long.parseLong;
import static java.util.function.Function.identity;
import static one.util.streamex.StreamEx.of;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@UtilityClass
class MongoChangeLogUtils {

    void initDb(
            String authorsFile,
            String genresFile,
            String booksFile,
            String bookToAuthorFile,
            String bookToGenreFile,
            String commentsFile,
            MongoOperations mongoOps
    ) {
        var authors = readFromCSV(
                authorsFile,
                Author.class,
                a -> a.setId(newId(mongoOps, "author")),
                "firstName",
                "lastName"
        );
        mongoOps.insertAll(authors);

        var genres = readFromCSV(
                genresFile,
                Genre.class,
                g -> g.setId(newId(mongoOps, "genre")),
                "name"
        );
        mongoOps.insertAll(genres);

        var books = readFromCSV(
                booksFile,
                Book.class,
                book -> book.setId(newId(mongoOps, "book")),
                "title",
                "isbn"
        );
        var bookMap = of(books).mapToEntry(Book::getId, identity()).toMap();

        var authorMap = of(authors).mapToEntry(Author::getId, identity()).toMap();
        readPairsFromCSV(bookToAuthorFile)
                .forEach(p -> {
                    var book = bookMap.get(parseLong(p.getFirst()));
                    var author = authorMap.get(parseLong(p.getSecond()));
                    if (book.getAuthors() == null) {
                        book.setAuthors(new HashSet<>());
                    }
                    book.getAuthors().add(author);
                });

        var genreMap = of(genres).mapToEntry(Genre::getId, identity()).toMap();
        readPairsFromCSV(bookToGenreFile)
                .forEach(p -> {
                    var book = bookMap.get(parseLong(p.getFirst()));
                    var genre = genreMap.get(parseLong(p.getSecond()));
                    if (book.getGenres() == null) {
                        book.setGenres(new HashSet<>());
                    }
                    book.getGenres().add(genre);
                });

        mongoOps.insertAll(books);

        var commentsMap = new HashMap<Long, BookComments>();
        readPairsFromCSV(commentsFile)
                .forEach(p -> {
                    var text = p.getSecond();
                    var bookId = parseLong(p.getFirst());
                    var comment = new MongoComment(newId(mongoOps, "comment"), text, new Date());
                    commentsMap.computeIfAbsent(bookId, (id) -> new BookComments(id, new HashSet<>()));
                    commentsMap.get(bookId).getComments().add(comment);
                });

        commentsMap.forEach(
                (bookId, comments) -> mongoOps.updateFirst(
                        query(where("_id").is(bookId)),
                        Update.update("comments", comments.getComments()),
                        BookComments.class
                )
        );
    }


    @SneakyThrows
    private List<Pair<String, String>> readPairsFromCSV(String fileName) {
        var mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
        try (
                var file = new ClassPathResource(fileName).getInputStream();
                MappingIterator<String[]> it = mapper
                        .readerFor(String[].class)
                        .readValues(file)
        ) {
            return of(it).skip(1L).map(
                    arr -> Pair.of(arr[0], arr[1])
            ).toList();
        }
    }

    @SneakyThrows
    private <T> List<T> readFromCSV(
            String fileName,
            Class<T> clz,
            Consumer<T> idSetter,
            String... fields
    ) {
        var mapper = new CsvMapper();
        try (
                var file = new ClassPathResource(fileName).getInputStream();
                MappingIterator<T> objItr = mapper
                        .readerFor(clz)
                        .with(
                                of(fields).foldLeft(builder(), CsvSchema.Builder::addColumn)
                                        .build()
                                        .withHeader()
                        )
                        .readValues(file)
        ) {
            var objs = objItr.readAll();
            objs.forEach(idSetter);
            return objs;
        }
    }

    private Long newId(MongoOperations mongoOps, String collectionName) {
        return new SequenceRepository(mongoOps).getNextSequence(collectionName);
    }
}
