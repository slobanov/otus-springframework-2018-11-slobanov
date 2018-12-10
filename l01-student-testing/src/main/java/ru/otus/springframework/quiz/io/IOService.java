package ru.otus.springframework.quiz.io;

public interface IOService {
    String readLine();
    void writeLine(String text);

    default String ask(String text) {
        writeLine(text);
        return readLine();
    }
}
