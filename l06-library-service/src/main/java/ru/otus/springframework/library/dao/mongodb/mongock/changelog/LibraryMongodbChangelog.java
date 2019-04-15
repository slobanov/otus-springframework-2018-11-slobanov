package ru.otus.springframework.library.dao.mongodb.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@ChangeLog
@Service
@Profile("mongodb")
public class LibraryMongodbChangelog {

    @ChangeSet(order = "001", id = "changelog-1.0", author = "s.lobanov")
    @SneakyThrows
    public static void addInitialData(MongoOperations mongoOps) {
        MongoChangeLogUtils.initDb(
                "db/changelog/data/author.csv",
                "db/changelog/data/genre.csv",
                "db/changelog/data/book.csv",
                "db/changelog/data/book_to_author.csv",
                "db/changelog/data/book_to_genre.csv",
                "db/changelog/data/comment.csv",
                mongoOps
        );
    }

    @ChangeSet(order = "002", id = "changelog-2.0", author = "s.lobanov")
    public static void addUsers(MongoOperations mongoOps) {
        MongoChangeLogUtils.addUsers("db/changelog/data/user.csv", mongoOps);
    }

}
