package ru.otus.springframework.library.delivery;

import org.springframework.stereotype.Service;
import ru.otus.springframework.library.books.Book;

import java.time.LocalDate;

@Service
public class DeliveryService {

    public DeliveryTicket bookDelivery(Book book) {
        return new DeliveryTicket(book, LocalDate.now().plusDays(1L));
    }

}
