package ru.otus.springframework.library.dao.jpa;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.dao.SimpleDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

@Slf4j
class SimpleDAOJpa<T> implements SimpleDAO<T> {

    private final Class<T> persistentClass;
    @Getter(PACKAGE)
    private final String selectQuery;

    @PersistenceContext
    @Getter(PACKAGE)
    private EntityManager em;

    SimpleDAOJpa(Class<T> persistentClass) {
        this(persistentClass, format("SELECT o FROM %s o", persistentClass.getSimpleName()));
    }

    SimpleDAOJpa(Class<T> persistentClass, String selectQuery) {
        this.persistentClass = persistentClass;
        this.selectQuery = selectQuery;
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
