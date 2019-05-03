package ru.otus.springframework.library.actuator.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.books.flux.BookServiceFlux;

@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(BookServiceFlux.class)
public class BookServiceHealthIndicator implements HealthIndicator {

    private final BookService bookService;
  
    @Override
    public Health health() {
        try {
            bookService.all();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("exception", e).build();
        }
    }
     
}