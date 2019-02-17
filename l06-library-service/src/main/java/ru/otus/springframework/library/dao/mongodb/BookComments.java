package ru.otus.springframework.library.dao.mongodb;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "book")
public class BookComments {
    @Id
    private final Long id;

    private final @NonNull Set<MongoComment> comments;
}

