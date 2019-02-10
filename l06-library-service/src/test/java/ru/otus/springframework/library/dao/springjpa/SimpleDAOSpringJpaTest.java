package ru.otus.springframework.library.dao.springjpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;

@DataJpaTest
@EntityScan(basePackages = "ru.otus.springframework.library")
@ActiveProfiles({"test", "test-spring-jpa"})
class SimpleDAOSpringJpaTest extends SimpleDAOBaseTest {}