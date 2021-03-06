package ru.otus.springframework.library.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

class OptionalUtilsTest {

    @Test
    void asSingleMultiple() {
        var list = List.of(1, 2, 3);
        assertThrows(IncorrectResultSizeDataAccessException.class, () -> asSingle(list));
    }

    @ParameterizedTest
    @ValueSource(ints = {-10, 1, 42})
    void asSinglePresent(int val) {
        var list = List.of(val);
        var single = asSingle(list);

        assertThat(single.isPresent(), equalTo(true));
        assertThat(single.get(), equalTo(val));
    }

    @Test
    void asSingleEmpty() {
        assertThat(asSingle(List.of()), equalTo(Optional.empty()));
    }

}