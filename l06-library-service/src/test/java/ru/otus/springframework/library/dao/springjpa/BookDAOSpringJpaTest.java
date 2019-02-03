package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@DataJpaTest
@EntityScan(basePackages = "ru.otus.springframework.library")
@ActiveProfiles({"test", "test-spring-jpa"})
class BookDAOSpringJpaTest extends BookDaoBaseTest {
}