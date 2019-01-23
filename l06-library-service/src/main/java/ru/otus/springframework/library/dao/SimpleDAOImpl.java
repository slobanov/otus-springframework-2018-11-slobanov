package ru.otus.springframework.library.dao;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Map.entry;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Slf4j
class SimpleDAOImpl<T> implements SimpleDAO<T> {

    private final String tableName;
    private final RowMapper<T> rowMapper;
    private final Map<String, String> sqlParams;

    private final NamedParameterJdbcOperations jdbcOperations;

    SimpleDAOImpl(
            String tableName,
            RowMapper<T> rowMapper,
            Map<String, String> sqlParams, NamedParameterJdbcOperations jdbcOperations
    ) {
        this.tableName = tableName;
        this.rowMapper = rowMapper;
        this.sqlParams = new HashMap<>(sqlParams);
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<T> fetchAll() {
        log.debug("fetchAll");
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
    public List<T> findByField(String fieldName, String fieldValue) {
        log.debug("findByField[{}]: fieldName = {}; fieldValue = {}", tableName, fieldName, fieldValue);
        return jdbcOperations.query(
                format("SELECT * FROM %s WHERE %s = :%s", tableName, fieldName, fieldName),
                of(fieldName, fieldValue),
                rowMapper
        );
    }

    @Override
    public T save(T obj) {
        log.debug("save[{}]: {}", tableName, obj);
        var insertParams = EntryStream.of(sqlParams).mapValues(v -> ':' + v)
                .reduce((acc, curr) -> entry(
                        acc.getKey() + ", " + curr.getKey(),
                        acc.getValue() + ", " + curr.getValue()
                )).map(e -> format("(%s) VALUES (%s)", e.getKey(), e.getValue())).orElse("");

        var keyHolder = new GeneratedKeyHolder();
        var sqlProperties = new BeanPropertySqlParameterSource(obj);

        jdbcOperations.update(
                "INSERT INTO " + tableName + insertParams,
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
    public Optional<T> deleteById(Long id) {
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
