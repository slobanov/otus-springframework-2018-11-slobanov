package ru.otus.springframework.library.authors;

import one.util.streamex.EntryStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.dao.DataIntegrityViolationException;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    private AuthorService authorService;

    private SimpleDAO<Author> authorDAO;

    @BeforeEach
    void init() {
        authorDAO = (SimpleDAO<Author>) mock(SimpleDAO.class);
        authorService = new AuthorServiceImpl(authorDAO);
    }

    @Test
    void all() {
        authorService.all();
        verify(authorDAO).fetchAll();
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void withId(Long id, Optional<Author> expectedAuthor) {
        when(authorDAO.findById(id)).thenReturn(expectedAuthor);

        var author = authorService.withId(id);
        verify(authorDAO).findById(id);
        
        assertThat(author, equalTo(expectedAuthor));

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
    @CsvSource({
            "fName1,lName1",
            "fName2,lName2"
    })
    void newAuthor(String fName, String lName) {
        var authorCaptor = ArgumentCaptor.forClass(Author.class);
        authorService.newAuthor(fName, lName);
        verify(authorDAO).save(authorCaptor.capture());
        var author = authorCaptor.getValue();

        assertThat(fName, equalTo(author.getFirstName()));
        assertThat(lName, equalTo(author.getLastName()));
    }

    @Test
    void newAuthorFailed() {
        when(authorDAO.save(ArgumentMatchers.any(Author.class))).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> authorService.newAuthor("1", "2"));
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void removeAuthor(Long id, Optional<Author> expectedAuthor) {
        when(authorDAO.deleteById(id)).thenReturn(expectedAuthor);

        assertThat(authorService.removeAuthor(id), equalTo(expectedAuthor));
        verify(authorDAO).deleteById(id);
    }

    @Test
    void removeAuthorFailed() {
        when(authorDAO.deleteById(anyLong())).thenThrow(DataIntegrityViolationException.class);
        var id = 1L;
        assertThrows(IllegalArgumentException.class, () -> authorService.removeAuthor(id));
        verify(authorDAO).deleteById(id);
    }
}