package ru.otus.springframework.library.dao.mongodb;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;

import java.util.Date;

@Data
public class MongoComment {
    @Id
    private final @NonNull Long id;
    private final @NonNull String text;
    private final @NonNull Date created;

    Comment asComment(Book book) {
        return new Comment(id, book, text, created);
    }

    static MongoComment fromComment(Comment comment) {
        return new MongoComment(comment.getId(), comment.getText(), comment.getCreated());
    }
}
