package ru.otus.springframework.quiz.auth;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.io.IOService;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class IOAuthServiceTest {

    @MockBean
    private IOService ioService;

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