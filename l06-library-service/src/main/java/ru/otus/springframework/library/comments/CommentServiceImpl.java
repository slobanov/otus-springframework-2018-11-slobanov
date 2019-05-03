package ru.otus.springframework.library.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.comments.flux.CommentServiceFlux;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.CommentDAO;

import java.util.Collections;
import java.util.List;

@Service("commentService")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnMissingBean(CommentServiceFlux.class)
class CommentServiceImpl implements CommentService {

    private final BookDAO bookDAO;
    private final CommentDAO commentDAO;

    @Override
    public List<Comment> commentsFor(String isbn) {
        var book = bookDAO.findByIsbn(isbn);
        return book.map(
                bk -> commentDAO.findByBookId(bk.getId())
        ).orElse(Collections.emptyList());
    }

    @Override
    public Comment newComment(@Payload String isbn, @Header String text) {
        log.debug("new comment for book with isbn [{}]: {}", isbn, text);
        var book = bookDAO.findByIsbn(isbn);
        log.debug("book with isbn [{}]: {}", isbn, book);
        return book.map(bk -> {
            var comment = new Comment(bk, text);
            return commentDAO.saveObj(comment);
        }).orElseThrow(
                () -> new IllegalArgumentException("There is no book with isbn = " + isbn)
        );
    }
}
