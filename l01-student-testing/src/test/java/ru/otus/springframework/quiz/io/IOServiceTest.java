package ru.otus.springframework.quiz.io;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;

class IOServiceTest {

    @ParameterizedTest
    @ValueSource(strings = {"1some string", "\u1234 utf-8 char"})
    void ask(String text) {
        var ioService = spy(IOService.class);
        var inOrder = inOrder(ioService);

        ioService.ask(text);
        inOrder.verify(ioService).writeLine(text);
        inOrder.verify(ioService).readLine();
        inOrder.verifyNoMoreInteractions();
    }
}