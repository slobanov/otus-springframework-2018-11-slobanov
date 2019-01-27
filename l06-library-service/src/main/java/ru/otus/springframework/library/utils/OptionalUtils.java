package ru.otus.springframework.library.utils;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class OptionalUtils {

    private OptionalUtils(){}

    public static <T> Optional<T> asSingle(Collection<T> collection) {
        Objects.requireNonNull(collection);
        if (collection.isEmpty()) {
            return Optional.empty();
        } else if (collection.size() == 1) {
            return Optional.of(collection.iterator().next());
        } else {
            throw new IncorrectResultSizeDataAccessException(1);
        }
    }

}
