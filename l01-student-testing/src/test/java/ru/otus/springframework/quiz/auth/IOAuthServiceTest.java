package ru.otus.springframework.quiz.auth;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.otus.springframework.quiz.io.IOService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IOAuthServiceTest {

    @ParameterizedTest
    @CsvSource({
            "fName text, lastName text, fName, lastName",
            "What's your first name?, What's your last name?, Ivan, Ivanov"
    })
    void authorize(
            String fNameText,
            String lNameText,
            String firstName,
            String lastName
    ) {
        var ioService = mock(IOService.class);
        var authService = new IOAuthService(
                ioService,
                fNameText,
                lNameText
        );

        when(ioService.ask(fNameText)).thenReturn(firstName);
        when(ioService.ask(lNameText)).thenReturn(lastName);

        assertThat(authService.authorize(), equalTo(new Student(firstName, lastName)));
    }
}