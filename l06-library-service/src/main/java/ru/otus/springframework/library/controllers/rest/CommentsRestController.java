package ru.otus.springframework.library.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.CommentService;

import java.util.List;

@RestController
@Profile("rest")
@RequiredArgsConstructor
public class CommentsRestController {

    private final CommentService commentService;

    @GetMapping("/api/v2/comments/{isbn}")
    public List<Comment> comments(@PathVariable String isbn) {
        return commentService.commentsFor(isbn);
    }

}
