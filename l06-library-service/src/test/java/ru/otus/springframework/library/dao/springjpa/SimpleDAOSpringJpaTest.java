package ru.otus.springframework.library.dao.springjpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.SimpleDAO;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@EntityScan(basePackages = "ru.otus.springframework.library")
@ActiveProfiles({"test", "test-spring-jpa"})
class SimpleDAOSpringJpaTest extends SimpleDAOBaseTest {

    @Autowired
    private SimpleDAO<Comment> commentDAO;

    @Test
    void findCommentByText() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> commentDAO.findByField("TEXT", "rly?")
        );
    }
}