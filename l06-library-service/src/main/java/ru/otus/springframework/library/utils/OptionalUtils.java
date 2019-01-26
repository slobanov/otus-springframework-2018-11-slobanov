package ru.otus.springframework.library.utils;

import one.util.streamex.StreamEx;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class OptionalUtils {

    private OptionalUtils(){}

    public static <T> Optional<T> asSingle(List<T> list) {
        Objects.requireNonNull(list);
        if (list.isEmpty()) {
            return Optional.empty();
        } else if (list.size() == 1) {
            return Optional.of(list.get(0));
        } else {
            throw new IncorrectResultSizeDataAccessException(1);
        }
    }

    public static <T> List<T> flatten(StreamEx<Optional<T>> stream) {
        return stream.flatMap(Optional::stream).toList();
    }

}
