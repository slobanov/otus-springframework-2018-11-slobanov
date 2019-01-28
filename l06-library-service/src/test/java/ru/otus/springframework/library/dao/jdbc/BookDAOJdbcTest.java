package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@DataJdbcTest
@ActiveProfiles({"test", "test-jdbc"})
@ContextConfiguration(classes = JdbcDAOConfig.class)
@Import(BookDAOJdbc.class)
class BookDAOJdbcTest extends BookDaoBaseTest {
}