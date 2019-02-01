package ru.otus.springframework.library.cli.presenters;

import one.util.streamex.EntryStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class PresenterServiceTest {

    private PresenterService presenterService;

    @BeforeEach
    void init() {
        presenterService = spy(new PresenterServiceImpl(List.of(
                new PresenterImpl<>(String.class, () -> EntryStream.of(Map.of("a", "b")))
        )));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", ""})
    void presentSingle(String val) {
        presenterService.present(val, String.class);
        verify(presenterService).present(List.of(val), String.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", ""})
    void presentOptional(String val) {
        presenterService.present(Optional.of(val), String.class);
        verify(presenterService).present(List.of(val), String.class);
    }

    @Test
    void presentEmptyOptional() {
        presenterService.present(Optional.empty(), String.class);
        verify(presenterService).present(List.of(), String.class);
    }

    @Test
    void noPresenter() {
        assertThrows(
                IllegalArgumentException.class,
                () -> presenterService.present(1L, Long.class)
        );
    }

}