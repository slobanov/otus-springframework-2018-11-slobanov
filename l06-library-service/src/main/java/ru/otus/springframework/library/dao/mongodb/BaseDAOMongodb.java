package ru.otus.springframework.library.dao.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.Optional;

@NoRepositoryBean
interface BaseDAOMongodb<T> extends MongoRepository<T, Long>, SimpleDAO<T> {

    @Override
    Optional<T> findById(Long id);

    @Override
    default Optional<T> deleteByObjId(Long id) {
        var obj = findById(id);
        deleteById(id);
        return obj;
    }
}

interface CustomSave<T> {
    T saveObj(T obj);
}
