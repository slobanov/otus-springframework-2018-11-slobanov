package ru.otus.springframework.quiz.auth;

import ru.otus.springframework.quiz.io.IOService;

public class IOAuthService implements AuthService {

    private final IOService ioService;
    private final String firstNameText;
    private final String lastNameText;

    public IOAuthService(
            IOService ioService,
            String firstNameText,
            String lastNameText
    ) {
        this.ioService = ioService;
        this.firstNameText = firstNameText;
        this.lastNameText = lastNameText;
    }

    @Override
    public Student authorize() {
        var firstName = ioService.ask(firstNameText);
        var lastName = ioService.ask(lastNameText);

        return new Student(firstName, lastName);
    }

}
