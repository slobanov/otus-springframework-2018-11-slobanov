package ru.otus.springframework.library.dao;

import ru.otus.springframework.library.comments.Comment;

import java.util.List;

public interface CommentDAO extends SimpleDAO<Comment> {
    List<Comment> findByBookId(Long bookId);
}
