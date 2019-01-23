package ru.otus.springframework.library.cli.presenters;

import one.util.streamex.StreamEx;
import org.springframework.shell.table.Table;

import java.util.List;
import java.util.Optional;

public interface PresenterService {
    <T> Table present(List<T> elems, Class<? super T> clz);
    default <T> Table present(T elem, Class<? super T> clz) {
        return present(List.of(elem), clz);
    }
    default <T> Table present(Optional<T> elem, Class<? super T> clz) {
        return present(StreamEx.of(elem).toList(), clz);
    }
}
