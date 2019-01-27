package ru.otus.springframework.library.comments;

import ru.otus.springframework.library.books.Book;

import java.util.List;

public interface CommentService {
    List<Comment> commentsFor(String isbn);
    Book newComment(String isbn, String text);
}
