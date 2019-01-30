package ru.otus.springframework.library.comments;

import java.util.List;

public interface CommentService {
    List<Comment> commentsFor(String isbn);
    Comment newComment(String isbn, String text);
}
