package ru.otus.springframework.library.delivery;

import lombok.Data;
import ru.otus.springframework.library.books.Book;

import java.time.LocalDate;

@Data
public class DeliveryTicket {
    private final Book book;
    private final LocalDate estimatedDeliveryDate;
}
