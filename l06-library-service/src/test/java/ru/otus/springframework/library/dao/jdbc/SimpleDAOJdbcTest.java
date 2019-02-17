package ru.otus.springframework.library.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DataJdbcTest
@ActiveProfiles({"test", "test-jdbc"})
@Import({BookDAOJdbc.class, AuthorDAOJdbc.class, GenreDAOJdbc.class, CommentDAOJdbc.class})
@Transactional
class SimpleDAOJdbcTest extends SimpleDAOBaseTest {

    @SpyBean
    private AuthorDAO authorDAO;

    @Test
    void saveFail() {
        when(authorDAO.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> authorDAO.saveObj(new Author("1", "2")));
    }
}