package ru.otus.springframework.library.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;
import ru.otus.springframework.library.comments.CommentService;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceHealthIndicatorTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentServiceHealthIndicator commnetServiceHealthIndicator;

    @Test
    void healthUp() {
        when(commentService.commentsFor("")).thenReturn(List.of());
        assertThat(commnetServiceHealthIndicator.health().getStatus(), equalTo(Status.UP));
    }

    @Test
    void healthDown() {
        when(commentService.commentsFor("")).thenThrow(RuntimeException.class);
        assertThat(commnetServiceHealthIndicator.health().getStatus(), equalTo(Status.DOWN));
    }
}