package ru.otus.springframework.library.order;

class OrderException extends RuntimeException {
    OrderException(String text) {
        super(text);
    }
}
