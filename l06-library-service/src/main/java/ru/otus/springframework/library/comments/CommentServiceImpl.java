package ru.otus.springframework.library.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static one.util.streamex.StreamEx.of;

@Service
@RequiredArgsConstructor
class CommentServiceImpl implements CommentService {

    private final BookDAO bookDAO;
    private final SimpleDAO<Comment> commentDAO;

    @Override
    public List<Comment> commentsFor(String isbn) {
        var book = bookDAO.findByIsbn(isbn);
        return book.map(bk -> of(bk.getComments())
                .sortedBy(Comment::getCreated)
                .toList()
        ).orElse(Collections.emptyList());
    }

    @Override
    public Book newComment(String isbn, String text) {
        var book = bookDAO.findByIsbn(isbn);
        return book.map(bk -> {
            var comment = new Comment(bk.getId(), text);
            commentDAO.save(comment);
            return bookDAO.findById(bk.getId())
                    .orElseThrow(() -> new IllegalStateException(format(
                            "Something went really wrong while saving book [%s] with comment [%s]",
                            bk, comment
                    )));
        }).orElseThrow(
                () -> new IllegalArgumentException("There is no book with isbn = " + isbn)
        );
    }
}
