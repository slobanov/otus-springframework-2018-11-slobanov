package ru.otus.springframework.library.comments.flux;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.reactive.ReactiveBookMongodbRepository;
import ru.otus.springframework.library.dao.reactive.ReactiveCommentMongodbRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceFluxImplTest {

    @Mock
    private ReactiveCommentMongodbRepository commentRepository;

    @Mock
    private ReactiveBookMongodbRepository bookRepository;

    @InjectMocks
    private CommentServiceFluxImpl commentService;

    @ParameterizedTest
    @MethodSource("commentProvider")
    void commentsFor(List<Comment> expectedComments) {
        var isbn = "isbn";
        when(commentRepository.findByBookId(anyLong())).thenReturn(Flux.fromIterable(expectedComments));
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.just(mock(Book.class)));

        StepVerifier.create(commentService.commentsFor(isbn).collectList())
                .assertNext(comments -> assertThat(comments, equalTo(expectedComments)))
                .verifyComplete();
    }

    @Test
    void commentsForNoBook() {
        when(bookRepository.findByIsbn(anyString())).thenReturn(Mono.empty());
        StepVerifier.create(commentService.commentsFor("213"))
                .verifyComplete();
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
        var expectedComment = mock(Comment.class);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.just(mock(Book.class)));
        when(commentRepository.saveObj(any(Comment.class))).thenReturn(Mono.just(expectedComment));

        StepVerifier.create(commentService.newComment(isbn, text))
                .assertNext(comment -> assertThat(comment, equalTo(expectedComment)))
                .verifyComplete();

        var commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).saveObj(commentCaptor.capture());
        var comment = commentCaptor.getValue();

        assertThat(comment.getText(), equalTo(text));
    }

    @Test
    void newCommentNoBook() {
        var isbn = "isbn";
        when(bookRepository.findByIsbn(isbn)).thenReturn(Mono.empty());

        StepVerifier.create(commentService.newComment(isbn, "123"))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}