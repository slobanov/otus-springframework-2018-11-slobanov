package ru.otus.springframework.library.dao.jpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.dao.SimpleDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static java.lang.String.format;

@Slf4j
class SimpleDAOJpa<T> implements SimpleDAO<T> {

    private final Class<T> persistentClass;
    private final String selectQuery;
    private final Function<String, String> nameTranslator;

    @PersistenceContext
    private EntityManager em;

    SimpleDAOJpa(Class<T> persistentClass, UnaryOperator<String> nameTranslator) {
        this.persistentClass = persistentClass;
        this.nameTranslator = nameTranslator;
        this.selectQuery = format("SELECT o FROM %s o", persistentClass.getSimpleName());
    }

    @Override
    public List<T> fetchAll() {
        log.debug("fetch all [{}]", persistentClass);
        var query = em.createQuery(selectQuery, persistentClass);
        return query.getResultList();
    }

    @Override
    public Optional<T> findById(Long id) {
        log.debug("find by id [{}]: {}", persistentClass, id);
        var found = em.find(persistentClass, id);
        log.debug("found [{}]: {}", persistentClass, found);
        return Optional.ofNullable(found);
    }

    @Override
    public List<T> findByField(String fieldName, String fieldValue) {
        log.debug("find by field [{}]: {} = {}", persistentClass, fieldName, fieldValue);
        var translatedName = nameTranslator.apply(fieldName);
        var query = em.createQuery(
                selectQuery + format(" WHERE o.%s = :%s", translatedName, fieldName),
                persistentClass
        ).setParameter(fieldName, fieldValue);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Optional<T> deleteById(Long id) {
        log.debug("delete by id [{}]: {}", persistentClass, id);
        var obj = findById(id);
        log.debug("obj to delete [{}]: {}", persistentClass, obj);
        obj.ifPresent(em::remove);
        return obj;
    }

    @Override
    @Transactional
    public T save(T obj) {
        log.debug("saving [{}]: {}", persistentClass, obj);
        em.persist(obj);
        log.debug("saved [{}]: {}", persistentClass, obj);
        return obj;
    }
}
