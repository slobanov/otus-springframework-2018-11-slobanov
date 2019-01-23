package ru.otus.springframework.library.dao;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleDAOImplTest {

    @SpyBean
    private SimpleDAO<Author> authorDAO;

    @Autowired
    private BookService bookService;

    @Autowired
    private SimpleDAO<Genre> genreDAO;

    @Test
    void fetchAll() {
        var allAuthors = authorDAO.fetchAll();
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
        author.ifPresent(a -> assertThat(authorDAO.fetchAll(), hasItem(a)));
    }

    private static Stream<Arguments> authorProvider() {
        return EntryStream.of(
                1L, new Author(1L, "fName1", "lName1"),
                2L, new Author(2L, "fName2", "lName1"),
                4L, new Author(4L, "fName4", "lName4"),
                42L, null
        ).mapValues(Optional::ofNullable).mapToValue((id, a) -> of(id, a)).values();
    }


    @ParameterizedTest
    @MethodSource("authorFieldProvider")
    void findByField(String fieldName, String fieldValue, List<Author> expected) {
        var actual = authorDAO.findByField(fieldName, fieldValue);
        assertThat(actual, equalTo(expected));
    }

    private static Stream<Arguments> authorFieldProvider() {
        return StreamEx.of(
                of("FIRST_NAME", "fName1", List.of(
                        new Author(1L, "fName1", "lName1")
                )),
                of("LAST_NAME", "lName1", List.of(
                        new Author(1L, "fName1", "lName1"),
                        new Author(2L, "fName2", "lName1")
                )),
                of("FIRST_NAME", "fName4", List.of(
                        new Author(4L, "fName4", "lName4"),
                        new Author(5L, "fName4", "lName5")
                )),
                of("FIRST_NAME", "fName42", List.of())
        );
    }

    @Test
    void save() {
        var firstName = "Test";
        var lastName = "Testov";

        var author = authorDAO.save(new Author("Test", "Testov"));

        assertThat(author.getFirstName(), equalTo(firstName));
        assertThat(author.getLastName(), equalTo(lastName));

        var allAuthors = authorDAO.fetchAll();
        assertThat(allAuthors, hasItem(author));
        assertThat(allAuthors, hasSize(6));
    }

    @Test
    void saveFail() {
        when(authorDAO.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> authorDAO.save(new Author("1", "2")));
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void deleteById(Long id, Optional<Author> expectedAuthor) {
        if (bookService.writtenBy(id).isEmpty()) {
            var author = authorDAO.deleteById(id);
            assertThat(author, equalTo(expectedAuthor));
            author.ifPresent(a -> assertThat(authorDAO.fetchAll(), not(hasItem(a))));
        } else {
            assertThrows(DataIntegrityViolationException.class, () -> authorDAO.deleteById(id));
        }
    }

    @Test
    void saveSQLFail() {
        assertThrows(DuplicateKeyException.class, () -> genreDAO.save(new Genre("genre1")));
    }

}