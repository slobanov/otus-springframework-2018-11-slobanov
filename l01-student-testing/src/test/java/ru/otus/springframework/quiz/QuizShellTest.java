package ru.otus.springframework.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.Shell;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.springframework.quiz.auth.ShellAuthService;
import ru.otus.springframework.quiz.report.ShellReportService;

import static org.mockito.Mockito.*;

@SpringBootTest
@Profile("shell")
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QuizShellTest {

    @Autowired
    private Shell shell;

    @MockBean(reset = MockReset.BEFORE)
    private ShellAuthService authService;

    @MockBean(reset = MockReset.BEFORE)
    private QuizApplication quizApplication;

    @MockBean
    private ShellReportService shellReportService;

    void login(String firstName, String lastName) {
        shell.evaluate(() -> String.format("login -f %s -l %s", firstName, lastName));
    }

    void start() {
        shell.evaluate(() -> "start");
    }

    void result() {
        shell.evaluate(() -> "result");
    }


    @Test
    void happyPath() {
        var fName = "Test";
        var lName = "Testov";

        login(fName, lName);
        verify(authService).useAuthData(fName, lName);

        start();
        verify(quizApplication).performQuiz();

        result();
        verify(shellReportService).getReport();
    }

    @Test
    void singleResult() {
        result();
        verifyNoMoreInteractions(shellReportService);
    }

    @Test
    void noLogin() {
        start();
        verifyNoMoreInteractions(quizApplication);

        result();
        verifyNoMoreInteractions(shellReportService);
    }

    @Test
    void noQuiz() {
        var fName = "Test";
        var lName = "Testov";

        login(fName, lName);
        verify(authService).useAuthData(fName, lName);

        result();
        verifyNoMoreInteractions(shellReportService);
    }

    @Test
    void complex() {
        var fName = "Test";
        var lName = "Testov";

        login(fName, lName);
        verify(authService, times(1)).useAuthData(fName, lName);

        var fName2 = "1";
        var lName2 = "2";
        login(fName2, lName2);
        verify(authService, times(1)).useAuthData(fName, lName);

        start();
        verify(quizApplication, times(1)).performQuiz();

        login(fName, lName);
        verify(authService, times(2)).useAuthData(fName, lName);

        result();
        verify(shellReportService, never()).getReport();

        start();
        verify(quizApplication, times(2)).performQuiz();

        result();
        verify(shellReportService, times(1)).getReport();

        result();
        verify(shellReportService, times(2)).getReport();
    }

}