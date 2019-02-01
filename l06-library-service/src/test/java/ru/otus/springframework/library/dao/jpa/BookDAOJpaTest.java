package ru.otus.springframework.library.dao.jpa;

import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.otus.springframework.library.dao.BookDaoBaseTest;

@DataJpaTest
@EntityScan(basePackages = "ru.otus.springframework.library")
@ActiveProfiles({"test", "test-jpa"})
@AutoConfigurationPackage
@ContextConfiguration(classes = JpaDAOConfig.class)
@Import(BookDAOJpa.class)
class BookDAOJpaTest extends BookDaoBaseTest {
}