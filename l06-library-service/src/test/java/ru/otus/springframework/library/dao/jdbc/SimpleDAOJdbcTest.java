package ru.otus.springframework.library.dao.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles({"test", "test-jdbc"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SimpleDAOJdbcTest extends SimpleDAOBaseTest {

    @Test
    void saveFail() {
        when(authorDAO.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> authorDAO.save(new Author("1", "2")));
    }
}