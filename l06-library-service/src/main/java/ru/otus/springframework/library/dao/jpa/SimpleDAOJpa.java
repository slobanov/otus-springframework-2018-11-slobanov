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
    private final UnaryOperator<String> nameTranslator;
    private final Function<String, Function<String, Object>> valueTranslator;

    @PersistenceContext
    private EntityManager em;

    SimpleDAOJpa(
            Class<T> persistentClass,
            UnaryOperator<String> nameTranslator,
            Function<String, Function<String, Object>> valueTranslator
    ) {
        this.persistentClass = persistentClass;
        this.selectQuery = format("SELECT o FROM %s o", persistentClass.getSimpleName());
        this.nameTranslator = nameTranslator;
        this.valueTranslator = valueTranslator;
    }

    SimpleDAOJpa(Class<T> persistentClass, UnaryOperator<String> nameTranslator) {
        this(persistentClass, nameTranslator, s -> (o -> o));
    }

    @Override
    public List<T> findAll() {
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
        ).setParameter(fieldName, valueTranslator.apply(fieldName).apply(fieldValue));
        return query.getResultList();
    }

    @Override
    @Transactional
    public Optional<T> deleteByObjId(Long id) {
        log.debug("delete by id [{}]: {}", persistentClass, id);
        var obj = findById(id);
        log.debug("obj to delete [{}]: {}", persistentClass, obj);
        obj.ifPresent(em::remove);
        return obj;
    }

    @Override
    @Transactional
    public T saveObj(T obj) {
        log.debug("saving [{}]: {}", persistentClass, obj);
        em.persist(obj);
        log.debug("saved [{}]: {}", persistentClass, obj);
        return obj;
    }
}
