package ru.otus.springframework.library.dao;

import java.util.List;
import java.util.Optional;

public interface SimpleDAO<T> {
    List<T> fetchAll();

    Optional<T> findById(Long id);
    List<T> findByField(String fieldName, String fieldValue);

    Optional<T> deleteById(Long id);

    T save(T obj);
}
