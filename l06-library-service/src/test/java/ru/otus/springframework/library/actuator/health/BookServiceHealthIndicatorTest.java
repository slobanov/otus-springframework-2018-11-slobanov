package ru.otus.springframework.library.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import ru.otus.springframework.library.books.BookService;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceHealthIndicatorTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookServiceHealthIndicator bookServiceHealthIndicator;

    @Test
    void healthUp() {
        when(bookService.all()).thenReturn(List.of());
        assertThat(bookServiceHealthIndicator.health().getStatus(), equalTo(Status.UP));
    }

    @Test
    void healthDown() {
        when(bookService.all()).thenThrow(RuntimeException.class);
        assertThat(bookServiceHealthIndicator.health().getStatus(), equalTo(Status.DOWN));
    }
}