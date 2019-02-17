package ru.otus.springframework.library.dao.mongodb;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.SimpleDAOBaseTest;
import ru.otus.springframework.library.dao.mongodb.mongock.MongockConfig;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;

@DataMongoTest
@ActiveProfiles({"test", "test-mongodb"})
@Import({SequenceRepository.class, MongockConfig.class, CommentDAOMongodb.class, DummyTransactionManager.class})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleDAOMongoTest extends SimpleDAOBaseTest {
}