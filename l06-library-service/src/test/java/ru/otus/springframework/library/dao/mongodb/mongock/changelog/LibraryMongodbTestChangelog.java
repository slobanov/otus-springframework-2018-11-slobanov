package ru.otus.springframework.library.dao.mongodb.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@ChangeLog
@Service
@Profile({"test-mongodb", "test-reactive-mongodb"})
public class LibraryMongodbTestChangelog {

    @ChangeSet(order = "001", id = "test-1.0", author = "s.lobanov")
    @SneakyThrows
    public static void addInitialData(MongoOperations mongoOps) {
        MongoChangeLogUtils.initDb(
                "db/changelog/data/test_author.csv",
                "db/changelog/data/test_genre.csv",
                "db/changelog/data/test_book.csv",
                "db/changelog/data/test_book_to_author.csv",
                "db/changelog/data/test_book_to_genre.csv",
                "db/changelog/data/test-comment.csv",
                mongoOps
        );
    }

}
