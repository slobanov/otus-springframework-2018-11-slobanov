package ru.otus.springframework.library.dao.jdbc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@SpringBootTest
@ActiveProfiles({"test", "test-jdbc"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class BookDAOJdbcTest extends BookDaoBaseTest {
}