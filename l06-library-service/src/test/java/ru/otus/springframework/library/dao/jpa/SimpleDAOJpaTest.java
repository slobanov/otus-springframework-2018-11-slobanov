package ru.otus.springframework.library.dao.jpa;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

@SpringBootTest
@ActiveProfiles({"test", "test-jpa"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SimpleDAOJpaTest extends SimpleDAOBaseTest {
}