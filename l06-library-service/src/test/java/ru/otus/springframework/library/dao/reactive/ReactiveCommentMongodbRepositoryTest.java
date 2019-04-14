package ru.otus.springframework.library.dao.reactive;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.mongodb.mongock.MongockConfig;
import ru.otus.springframework.library.genres.Genre;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.of;

@DataMongoTest
@ActiveProfiles({"test", "test-reactive-mongodb"})
@Import({ReactiveSequenceRepository.class, MongockConfig.class, ReactiveCommentMongodbRepositoryImpl.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReactiveCommentMongodbRepositoryTest {

    @Autowired
    private ReactiveCommentMongodbRepository commentRepository;

    @Autowired
    private ReactiveBookMongodbRepository bookRepository;

    @ParameterizedTest
    @MethodSource("commentsProvider")
    void findByBookId(Long bookId, Collection<Comment> expectedComments) {
        var comments = commentRepository.findByBookId(bookId).collectList();

        StepVerifier.create(comments)
                .assertNext(cs -> {
                    assertMapped(cs, expectedComments, Comment::getText);
                    assertMapped(cs, expectedComments, Comment::getBookId);
                    assertMapped(cs, expectedComments, Comment::getId);
                })
                .verifyComplete();
    }

    private static void assertMapped(
            Collection<Comment> actual,
            Collection<Comment> expected,
            Function<Comment, ?> mapper) {
        assertThat(
                StreamEx.of(actual).map(mapper).sorted().toList(),
                equalTo(StreamEx.of(expected).map(mapper).sorted().toList())
        );
    }

    private static Stream<Arguments> commentsProvider() {
        return StreamEx.of(
                of(1L,
                        List.of(new Comment(
                                        1L,
                                        new Book(1L, "1", "book1",
                                                Set.of(
                                                        new Author(1L, "fName1", "lName1"),
                                                        new Author(2L, "fName2", "lName1")
                                                ),
                                                Set.of(
                                                        new Genre(1L, "genre1"),
                                                        new Genre(2L, "genre2")
                                                )
                                        ),
                                        "comment1",
                                        new Date()
                                ),
                                new Comment(
                                        2L,
                                        new Book(1L, "1", "book1",
                                                Set.of(
                                                        new Author(1L, "fName1", "lName1"),
                                                        new Author(2L, "fName2", "lName1")
                                                ),
                                                Set.of(
                                                        new Genre(1L, "genre1"),
                                                        new Genre(2L, "genre2")
                                                )
                                        ),
                                        "comment2",
                                        new Date()
                                )
                        )
                ));
    }

    @Test
    void saveComment() {
        var commentText = "new comment";
        var bookId = 1L;
        var book = bookRepository.findById(bookId).block();

        var resultComment = commentRepository.saveObj(new Comment(book, commentText));

        StepVerifier.create(resultComment)
                .assertNext(comment -> assertThat(comment.getText(), equalTo(commentText)))
                .verifyComplete();

        StepVerifier.create(commentRepository.findByBookId(1L).collectList())
                .assertNext(comments -> assertThat(StreamEx.of(comments)
                        .findAny(ct -> ct.getText().equals("new comment"))
                        .isPresent(), equalTo(true)))
                .verifyComplete();
    }

}