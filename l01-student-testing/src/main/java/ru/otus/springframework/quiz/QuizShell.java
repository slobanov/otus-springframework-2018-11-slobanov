package ru.otus.springframework.quiz;

import one.util.streamex.StreamEx;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.*;
import ru.otus.springframework.quiz.auth.ShellAuthService;
import ru.otus.springframework.quiz.report.ShellReportService;

import javax.validation.constraints.NotBlank;

import static org.springframework.shell.Availability.available;
import static org.springframework.shell.Availability.unavailable;

@ShellComponent
@Profile("shell")
class QuizShell {

    private final ShellAuthService shellAuthService;
    private final ShellReportService shellReportService;
    private final QuizApplication quizApplication;

    private QuizState quizState = QuizState.INITIAL;

    QuizShell(
            ShellAuthService shellAuthService,
            ShellReportService shellReportService,
            QuizApplication quizApplication
    ) {
        this.shellAuthService = shellAuthService;
        this.shellReportService = shellReportService;
        this.quizApplication = quizApplication;
    }

    @ShellMethod("Login for a quiz.")
    void login(
            @ShellOption({"-f", "--first-name"})@NotBlank String firstName,
            @ShellOption({"-l", "--last-name"})@NotBlank String lastName
    ) {
        shellAuthService.useAuthData(firstName, lastName);
        quizState = QuizState.LOGGED_IN;
    }

    @ShellMethod("Start a quiz. Don't forget to login first.")
    @ShellMethodAvailability("isReadyToStart")
    void start() {
        quizApplication.performQuiz();
        quizState = QuizState.QUIZ_COMPLETED;
    }

    @ShellMethod("Print your latest test result. This command available only if you passed the quiz.")
    @ShellMethodAvailability("canShowResult")
    Table result() {
        var data = StreamEx.of(shellReportService.getReport())
                .map(s -> new String[]{s})
                .toArray(String[].class);
        var model = new ArrayTableModel(data);
        var tableBuilder = new TableBuilder(model);

        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }

    private Availability isReadyToStart() {
        return quizState.isLoggedIn() ?
                available() : unavailable("you need to login first");
    }

    private Availability canShowResult() {
        return quizState.isQuizCompleted() ? available() : unavailable("you need to complete quiz first");
    }

    private enum QuizState {
        INITIAL,
        LOGGED_IN,
        QUIZ_COMPLETED;

        boolean isLoggedIn() {
            return checkState(LOGGED_IN);
        }

        boolean isQuizCompleted() {
            return checkState(QUIZ_COMPLETED);
        }

        private boolean checkState(QuizState state) {
            return state == this;
        }
    }
}

