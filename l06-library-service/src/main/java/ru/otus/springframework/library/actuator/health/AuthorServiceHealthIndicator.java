package ru.otus.springframework.library.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.authors.flux.AuthorServiceFlux;

@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(AuthorServiceFlux.class)
public class AuthorServiceHealthIndicator implements HealthIndicator {

    private final AuthorService authorService;
  
    @Override
    public Health health() {
        try {
            authorService.all();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("exception", e).build();
        }
    }
     
}