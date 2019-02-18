package ru.otus.springframework.library.dao;

import java.util.List;
import java.util.Optional;

public interface SimpleDAO<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
    Optional<T> deleteByObjId(Long id);
    T saveObj(T obj);
}
