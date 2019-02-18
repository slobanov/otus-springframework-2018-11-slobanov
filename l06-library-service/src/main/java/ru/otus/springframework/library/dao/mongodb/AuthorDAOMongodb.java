package ru.otus.springframework.library.dao.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.AuthorDAO;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
interface AuthorDAOMongodb extends BaseDAOMongodb<Author>, AuthorDAO, CustomSave<Author> {
}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
class AuthorDAOMongodbImpl implements CustomSave<Author> {

    private final SequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private AuthorDAOMongodb baseDAOMongodb;

    @Override
    public Author saveObj(Author obj) {
        obj.setId(sequenceRepository.getNextSequence("author"));
        return baseDAOMongodb.save(obj);
    }

}