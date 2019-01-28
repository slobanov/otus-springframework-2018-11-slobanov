package ru.otus.springframework.library.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@DataJdbcTest
@ActiveProfiles({"test", "test-jdbc"})
@ContextConfiguration(classes = JdbcDAOConfig.class)
@Import(BookDAOJdbc.class)
class SimpleDAOJdbcTest extends SimpleDAOBaseTest {

    @Test
    void saveFail() {
        when(authorDAO.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> authorDAO.save(new Author("1", "2")));
    }
}