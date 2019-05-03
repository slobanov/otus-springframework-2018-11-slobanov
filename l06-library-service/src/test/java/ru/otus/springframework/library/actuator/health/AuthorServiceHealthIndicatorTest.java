package ru.otus.springframework.library.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import ru.otus.springframework.library.authors.AuthorService;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceHealthIndicatorTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorServiceHealthIndicator authorServiceHealthIndicator;

    @Test
    void healthUp() {
        when(authorService.all()).thenReturn(List.of());
        assertThat(authorServiceHealthIndicator.health().getStatus(), equalTo(Status.UP));
    }

    @Test
    void healthDown() {
        when(authorService.all()).thenThrow(RuntimeException.class);
        assertThat(authorServiceHealthIndicator.health().getStatus(), equalTo(Status.DOWN));
    }
}