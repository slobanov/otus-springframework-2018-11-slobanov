package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@DataJdbcTest
@ActiveProfiles({"test", "test-jdbc"})
@Import({BookDAOJdbc.class, AuthorDAOJdbc.class, GenreDAOJdbc.class, CommentDAOJdbc.class})
class BookDAOJdbcTest extends BookDaoBaseTest {
}