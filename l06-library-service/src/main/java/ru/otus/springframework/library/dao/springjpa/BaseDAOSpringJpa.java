package ru.otus.springframework.library.dao.springjpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.Optional;

@NoRepositoryBean
interface BaseDAOSpringJpa<T> extends CrudRepository<T, Long>, SimpleDAO<T> {

    @Override
    Optional<T> findById(Long id);

    @Override
    default T saveObj(T obj) {
        return save(obj);
    }

    @Override
    default Optional<T> deleteByObjId(Long id) {
        var genre = findById(id);
        deleteById(id);
        return genre;
    }

}
