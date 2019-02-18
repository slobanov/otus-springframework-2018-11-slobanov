package ru.otus.springframework.library.comments;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.CommentDAO;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    private CommentService commentService;

    private CommentDAO commentDAO;
    private BookDAO bookDAO;

    @BeforeEach
    void init() {
        commentDAO = mock(CommentDAO.class);
        bookDAO = mock(BookDAO.class);

        commentService = new CommentServiceImpl(bookDAO, commentDAO);
    }

    @ParameterizedTest
    @MethodSource("commentProvider")
    void commentsFor(List<Comment> expectedComments) {
        var isbn = "isbn";
        when(commentDAO.findByBookId(anyLong())).thenReturn(expectedComments);
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(mock(Book.class)));

        var resultComments = commentService.commentsFor(isbn);
        assertThat(resultComments, contains(expectedComments.toArray()));
    }

    void commentsForNoBook() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());
        assertThat(commentService.commentsFor("213"), equalTo(Collections.emptyList()));
    }

    private static Stream<Arguments> commentProvider() {
        return StreamEx.of(
                Arguments.of(List.of(new Comment(1L, mock(Book.class), "1", new Date()))),
                Arguments.of(List.of(
                        new Comment(2L, mock(Book.class),"2", new Date(101L)),
                        new Comment(1L, mock(Book.class), "1", new Date(102L))
                        )
                )
        );
    }

    @Test
    void newComment() {
        var isbn = "isbn";
        var text = "text";
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(mock(Book.class)));
        when(commentDAO.saveObj(any(Comment.class))).thenReturn(mock(Comment.class));

        commentService.newComment(isbn, text);

        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentDAO).saveObj(commentCaptor.capture());
        var comment = commentCaptor.getValue();

        assertThat(comment.getText(), equalTo(text));
    }

    @Test
    void newCommentNoBook() {
        var isbn = "isbn";
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.newComment(isbn, "123")
        );
    }

}