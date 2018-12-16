package ru.otus.springframework.quiz.io;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

@Service
@Slf4j
class IOServiceImpl implements IOService {

    private final Scanner scanner;
    private final PrintStream printStream;

    IOServiceImpl(
            @Value("#{T(java.lang.System).in}") InputStream inputStream,
            @Value("#{T(java.lang.System).out}") PrintStream printStream
    ) {
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);
    }

    @Override
    public String readLine() {
        var text = scanner.nextLine();
        log.debug("input = {}", text);
        return text;
    }

    @Override
    public void writeLine(String text) {
        log.debug("text = {}", text);
        printStream.println(text);
    }
}
