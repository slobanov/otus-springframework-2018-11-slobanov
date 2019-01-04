package ru.otus.springframework.quiz.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ShellAuthServiceTest {

    @ParameterizedTest
    @CsvSource({
            "firstName,lastName",
            "123,qwe"
    })
    void authorize(String firstName, String lastName) {
        var authService = new ShellAuthService();
        authService.useAuthData(firstName, lastName);

        assertThat(authService.authorize(), equalTo(new Student(firstName, lastName)));
    }

    @Test
    void failedAuthorize() {
        assertThrows(IllegalStateException.class, () -> new ShellAuthService().authorize());
    }
}