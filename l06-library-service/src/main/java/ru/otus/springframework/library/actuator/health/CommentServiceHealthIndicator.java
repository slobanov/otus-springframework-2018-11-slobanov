package ru.otus.springframework.library.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.comments.flux.CommentServiceFlux;

@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(CommentServiceFlux.class)
public class CommentServiceHealthIndicator implements HealthIndicator {

    private final CommentService commentService;
  
    @Override
    public Health health() {
        try {
            commentService.commentsFor("");
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("exception", e).build();
        }
    }
     
}