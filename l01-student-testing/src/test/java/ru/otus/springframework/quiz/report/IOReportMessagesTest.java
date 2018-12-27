package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.question.Question;

import java.util.Map;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class IOReportMessagesTest {

    private IOReportMessages ioReportMessages;

    @BeforeEach
    void init() {
        Map<String, String> reportMessageMap = Map.of(
                "headerText", "header!",
                "resultText", "result!",
                "actualText", "actualText!",
                "expectedText", "expText!",
                "correctText", "correct!",
                "incorrectText", "incorrect!"
        );
        ioReportMessages = new IOReportMessages(reportMessageMap);
    }

    @ParameterizedTest
    @MethodSource("studentProvider")
    void formatHeader(Student student, String result) {
        assertThat(
                ioReportMessages.formatHeader(student),
                equalTo(result)
        );
    }

    private static Stream<Arguments> studentProvider() {
        return StreamEx.of(
                Arguments.of(new Student("Test", "Testov"),
                        "header!: Test Testov" + lineSeparator()),
                Arguments.of(new Student("Ivan", "Ivanov"),
                        "header!: Ivan Ivanov" + lineSeparator())
        );
    }

    @ParameterizedTest
    @MethodSource("answerProvider")
    void formatAnswer(Answer answer, String result) {
        assertThat(
                ioReportMessages.formatAnswer(answer),
                equalTo(result)
        );
    }

    private static Stream<Arguments> answerProvider() {
        return StreamEx.of(
                Arguments.of(
                    new Answer(new Question("1", "single", "one"),
                        "one"),
                        "1: single" + lineSeparator() +
                        "actualText!: one, expText!: one, correct!" + lineSeparator()
                ),
                Arguments.of(
                    new Answer(new Question("name1", "line text", ",,"),
                        ",,"),
                        "name1: line text" + lineSeparator() +
                        "actualText!: ,,, expText!: ,,, correct!" + lineSeparator()
                ),
                Arguments.of(
                    new Answer(new Question("name2", "other text", "new answer"),
                        "wrong answer"),
                        "name2: other text" + lineSeparator() +
                        "actualText!: wrong answer, expText!: new answer, incorrect!" + lineSeparator()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("resultProvider")
    void formatResult(int correct, int total, String result) {
        assertThat(ioReportMessages.formatResult(correct, total),
                equalTo(result));
    }

    private static Stream<Arguments> resultProvider() {
        return StreamEx.of(
                Arguments.of(1, 1, "result!: 1/1"),
                Arguments.of(3, 8, "result!: 3/8")
        );
    }

}