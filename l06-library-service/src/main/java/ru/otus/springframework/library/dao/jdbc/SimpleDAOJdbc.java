package ru.otus.springframework.library.dao.jdbc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Slf4j
@RequiredArgsConstructor
class SimpleDAOJdbc<T> implements SimpleDAO<T> {

    @Getter(AccessLevel.PACKAGE)
    private final NamedParameterJdbcOperations jdbcOperations;

    private final String tableName;
    private final String insertQueryTemplate;
    @Getter(AccessLevel.PACKAGE)
    private final RowMapper<T> rowMapper;

    @Override
    public List<T> findAll() {
        log.debug("findAll");
        return jdbcOperations.query(
                "SELECT * FROM " + tableName,
                rowMapper
        );
    }

    @Override
    public Optional<T> findById(Long id) {
        log.debug("findById[{}]: id = {}", tableName, id);
        return asSingle(jdbcOperations.query(
                "SELECT * FROM " + tableName + " WHERE ID = :ID",
                of("ID", id),
                rowMapper
        ));
    }

    @Override
    @Transactional
    public T saveObj(T obj) {
        log.debug("save[{}]: {}", tableName, obj);

        var keyHolder = new GeneratedKeyHolder();
        var sqlProperties = new BeanPropertySqlParameterSource(obj);

        jdbcOperations.update(
                insertQueryTemplate,
                sqlProperties,
                keyHolder,
                new String[] { "id" }
        );

        var id = requireNonNull(keyHolder.getKey()).longValue();
        log.debug("generated id: {}", id);

        return findById(id).orElseThrow(
                () -> new IllegalStateException("Failed to find saved object.")
        );
    }

    @Override
    @Transactional
    public Optional<T> deleteByObjId(Long id) {
        log.debug("delete by id[{}]: {}", tableName, id);
        var toDelete = findById(id);
        log.debug("obj to delete: {}", toDelete);

        jdbcOperations.update(
                "DELETE FROM " + tableName + " WHERE ID = :ID",
                of("ID", id)
        );
        return toDelete;
    }

}
