package ru.otus.springframework.library.dao.jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@DataJpaTest
@EntityScan(basePackages = "ru.otus.springframework.library")
@ActiveProfiles({"test", "test-jpa"})
@Import({BookDAOJpa.class, AuthorDAOJpa.class, GenreDAOJpa.class, CommentDAOJpa.class})
class BookDAOJpaTest extends BookDaoBaseTest {
}