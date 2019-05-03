package ru.otus.springframework.library.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import ru.otus.springframework.library.genres.GenreService;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceHealthIndicatorTest {

    @Mock
    private GenreService genreService;

    @InjectMocks
    private GenreServiceHealthIndicator genreServiceHealthIndicator;

    @Test
    void healthUp() {
        when(genreService.all()).thenReturn(List.of());
        assertThat(genreServiceHealthIndicator.health().getStatus(), equalTo(Status.UP));
    }

    @Test
    void healthDown() {
        when(genreService.all()).thenThrow(RuntimeException.class);
        assertThat(genreServiceHealthIndicator.health().getStatus(), equalTo(Status.DOWN));
    }
}