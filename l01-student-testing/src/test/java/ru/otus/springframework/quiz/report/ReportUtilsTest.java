package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.hamcrest.collection.IsArrayContaining;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.question.Question;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ReportUtilsTest {

    @MockBean
    private IOReportMessages ioReportMessages;

    @Autowired
    private ReportUtils reportUtils;

    @ParameterizedTest
    @MethodSource("answersProvider")
    void reportArray(List<Answer> answers, int correct) {
        var student = new Student("Test", "Testov");
        var studentRepr = student.getFirstName() + ' ' + student.getLastName();

        when(ioReportMessages.formatHeader(student)).thenReturn(studentRepr);
        answers.forEach(a -> when(
                ioReportMessages.formatAnswer(a)).thenReturn(a.getAnswerText()
        ));

        var resultRepr = correct + ":" + answers.size();
        when(ioReportMessages.formatResult(
                correct, answers.size())
        ).thenReturn(resultRepr);

        var report = reportUtils.reportStream(student, answers).toArray(String.class);

        answers.forEach(a -> {
            assertThat(report, IsArrayContaining.hasItemInArray(a.getAnswerText()));
        });
        assertThat(report, IsArrayContaining.hasItemInArray(resultRepr));
        assertThat(report, IsArrayContaining.hasItemInArray(studentRepr));

    }

    private static Stream<Arguments> answersProvider() {
        return StreamEx.of(
                Arguments.of(
                        List.of(
                                new Answer(new Question("1", "single", "one"),
                                        "one")
                        ), 1
                ),
                Arguments.of(List.of(), 0),
                Arguments.of(
                        List.of(
                                new Answer(new Question(
                                        "name1",
                                        "line text",
                                        ",,"
                                ), ",,"),
                                new Answer(new Question(
                                        "name2",
                                        "other text",
                                        "new answer"
                                ), "wrong answer")
                        ), 1
                )
        );
    }
}