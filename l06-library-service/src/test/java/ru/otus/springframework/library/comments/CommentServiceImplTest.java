package ru.otus.springframework.library.comments;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.*;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @MockBean
    private SimpleDAO<Comment> commentDAO;

    @MockBean
    private BookDAO bookDAO;

    @ParameterizedTest
    @MethodSource("commentProvider")
    void commentsFor(List<Comment> expectedComments) {
        var isbn = "isbn";
        var book = mock(Book.class);
        when(book.getComments()).thenReturn(new HashSet<>(expectedComments));
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(book));

        var resultComments = commentService.commentsFor(isbn);
        assertThat(resultComments, contains(expectedComments.toArray()));
    }

    void commentsForNoBook() {
        when(bookDAO.findByIsbn(anyString())).thenReturn(Optional.empty());
        assertThat(commentService.commentsFor("213"), equalTo(Collections.emptyList()));
    }

    private static Stream<Arguments> commentProvider() {
        return StreamEx.of(
                Arguments.of(List.of(new Comment(1L, 1L, "1", new Date()))),
                Arguments.of(List.of(
                        new Comment(2L, 2L, "2", new Date(101L)),
                        new Comment(1L, 1L, "1", new Date(102L)))
                )
        );
    }

    @Test
    void newComment() {
        var isbn = "isbn";
        var book = mock(Book.class);
        var text = "text";
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookDAO.findById(anyLong())).thenReturn(Optional.of(book));

        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        var resultBook = commentService.newComment(isbn, text);

        verify(commentDAO).save(commentCaptor.capture());
        var comment = commentCaptor.getValue();

        assertThat(resultBook, equalTo(book));
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

    @Test
    void newCommentFailedToFIndAfterSave() {
        var isbn = "isbn";
        var book = mock(Book.class);
        var text = "text";
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookDAO.findById(anyLong())).thenReturn(Optional.empty());

        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        assertThrows(IllegalStateException.class, () -> commentService.newComment(isbn, text));

        verify(commentDAO).save(commentCaptor.capture());
        var comment = commentCaptor.getValue();

        assertThat(comment.getText(), equalTo(text));
    }
}