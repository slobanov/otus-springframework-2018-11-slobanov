package ru.otus.springframework.library.dao;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.genres.Genre;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.of;

@Transactional
public abstract class SimpleDAOBaseTest {

    @Autowired
    private AuthorDAO authorDAO;

    @Autowired
    private BookDAO bookDAO;

    @Autowired
    private GenreDAO genreDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Test
    void fetchAll() {
        var allAuthors = authorDAO.findAll();
        assertThat(allAuthors, hasSize(5));
        assertThat(
                StreamEx.of(allAuthors).map(Author::getFirstName).toList(),
                contains("fName1", "fName2", "fName3", "fName4", "fName4")
        );
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void findById(Long id, Optional<Author> expectedAuthor) {
        var author = authorDAO.findById(id);
        assertThat(author, equalTo(expectedAuthor));
        author.ifPresent(a -> assertThat(authorDAO.findAll(), hasItem(a)));
    }

    private static Stream<Arguments> authorProvider() {
        return EntryStream.of(
                1L, new Author(1L, "fName1", "lName1"),
                2L, new Author(2L, "fName2", "lName1"),
                4L, new Author(4L, "fName4", "lName4"),
                42L, null
        ).mapValues(Optional::ofNullable).mapToValue((id, a) -> of(id, a)).values();
    }

    @Test
    void save() {
        var firstName = "Test";
        var lastName = "Testov";

        var author = authorDAO.saveObj(new Author("Test", "Testov"));

        assertThat(author.getFirstName(), equalTo(firstName));
        assertThat(author.getLastName(), equalTo(lastName));

        var allAuthors = authorDAO.findAll();
        assertThat(allAuthors, hasItem(author));
        assertThat(allAuthors, hasSize(6));
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void deleteById(Long id, Optional<Author> expectedAuthor) {
        if (expectedAuthor.isPresent() && bookDAO.findByAuthor(expectedAuthor.get()).isEmpty()) {
            var author = authorDAO.deleteByObjId(id);
            assertThat(author, equalTo(expectedAuthor));
            author.ifPresent(a -> assertThat(authorDAO.findAll(), not(hasItem(a))));
        }
    }

    @Test
    void saveSQLFail() {
        assertThrows(RuntimeException.class, () -> genreDAO.saveObj(new Genre("genre1")));
    }

    @Test
    void saveComment() {
        var commentText = "new comment";
        var bookId = 1L;
        var book = bookDAO.findById(bookId).orElseThrow();
        var commentsBefore = commentDAO.findAll();
        var resultComment = commentDAO.saveObj(new Comment(book, commentText));
        var commentsAfter = commentDAO.findAll();

        assertThat(resultComment.getText(), equalTo(commentText));
        assertThat(commentsAfter.size() - commentsBefore.size(), equalTo(1));
        assertThat(commentsBefore, not(contains(resultComment)));
        assertThat(commentsAfter, hasItem(resultComment));
    }

    @ParameterizedTest
    @MethodSource("commentsProvider")
    void findComment(Long bookId, Collection<Comment> expectedComments) {
        var comments = commentDAO.findByBookId(bookId);

        assertMapped(comments, expectedComments, Comment::getText);
        assertMapped(comments, expectedComments, Comment::getBook);
        assertMapped(comments, expectedComments, Comment::getId);
    }

    private static void assertMapped(
            Collection<Comment> actual,
            Collection<Comment> expected,
            Function<Comment, ?> mapper) {
        assertThat(
                StreamEx.of(actual).map(mapper).toList(),
                equalTo(StreamEx.of(expected).map(mapper).toList())
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

    @ParameterizedTest
    @MethodSource("genreProvider")
    void findGenre(String name, Genre expectedGenre) {
        var actualGenre = genreDAO.findByName(name);

        assertThat(actualGenre.isPresent(), equalTo(true));
        assertThat(actualGenre.get(), equalTo(expectedGenre));
    }

    private static Stream<Arguments> genreProvider() {
        return StreamEx.of(
                of("genre2", new Genre(2L, "genre2"))
        );
    }

}