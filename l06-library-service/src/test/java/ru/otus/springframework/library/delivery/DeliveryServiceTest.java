package ru.otus.springframework.library.delivery;

import org.junit.jupiter.api.Test;
import ru.otus.springframework.library.books.Book;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

class DeliveryServiceTest {

    private final DeliveryService deliveryService = new DeliveryService();

    @Test
    void bookDelivery() {
        var book = mock(Book.class);
        var ticket = deliveryService.bookDelivery(book);

        assertThat(ticket.getBook(), equalTo(book));
        assertThat(ticket.getEstimatedDeliveryDate(), equalTo(LocalDate.now().plusDays(1L)));
    }
}