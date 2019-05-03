package ru.otus.springframework.library.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.genres.GenreService;
import ru.otus.springframework.library.genres.flux.GenreServiceFlux;

@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(GenreServiceFlux.class)
public class GenreServiceHealthIndicator implements HealthIndicator {

    private final GenreService genreService;
  
    @Override
    public Health health() {
        try {
            genreService.all();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("exception", e).build();
        }
    }
     
}