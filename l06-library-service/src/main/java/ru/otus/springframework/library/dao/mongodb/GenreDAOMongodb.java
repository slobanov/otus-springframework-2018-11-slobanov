package ru.otus.springframework.library.dao.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.dao.mongodb.seq.SequenceRepository;
import ru.otus.springframework.library.genres.Genre;

@Repository
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
interface GenreDAOMongodb extends BaseDAOMongodb<Genre>, GenreDAO, CustomSave<Genre> {
}

@Repository
@RequiredArgsConstructor
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
class GenreDAOMongodbImpl implements CustomSave<Genre> {

    private final SequenceRepository sequenceRepository;

    @Autowired
    @Lazy
    private GenreDAOMongodb baseDAOMongodb;

    @Override
    public Genre saveObj(Genre obj) {
        obj.setId(sequenceRepository.getNextSequence("genre"));
        return baseDAOMongodb.save(obj);
    }

}
