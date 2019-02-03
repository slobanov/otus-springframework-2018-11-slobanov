package ru.otus.springframework.library.dao.springjpa;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

    @Override
    List<T> findAll();

    @Override
    default List<T> findByField(String fieldName, String fieldValue) {
        return fieldMapper().getOrDefault(fieldName, o -> {
                    throw new UnsupportedOperationException(
                            "findByField [" + o +"] is not supported for " + getClass().getSimpleName());
                }
        ).apply(fieldValue);
    }

    Map<String, Function<String, List<T>>> fieldMapper();

}
