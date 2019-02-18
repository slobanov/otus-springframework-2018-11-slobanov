package ru.otus.springframework.library.dao.mongodb.seq;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Document
public class Sequence {
    @Id
    private final @NonNull String id;
    private final @NonNull Long seq;
}