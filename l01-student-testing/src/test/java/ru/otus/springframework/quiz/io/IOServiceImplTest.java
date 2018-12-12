package ru.otus.springframework.quiz.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IOServiceImplTest {

    @ParameterizedTest
    @ValueSource(strings = {"1some string", "\u1234 utf-8 char"})
    void readLine(String text) {
        var ioService = getIOService(text);
        assertThat(ioService.readLine(), equalTo(text));
    }

    @Test
    void readEmpty() {
        var ioService = getIOService("");
        assertThrows(NoSuchElementException.class, ioService::readLine);
    }

    private static IOService getIOService(String input) {
        var inputStream = new ByteArrayInputStream(input.getBytes());
        return new IOServiceImpl(inputStream, null);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1some string", "\u1234 utf-8 char"})
    void writeLine(String text) {
        var printStream = mock(PrintStream.class);
        var ioService = new IOServiceImpl(mock(InputStream.class), printStream);
        ioService.writeLine(text);
        verify(printStream).println(text);
    }

}