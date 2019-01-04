package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.Question;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class IOReportServiceTest {

    @MockBean
    private IOService ioService;

    @MockBean
    private ReportUtils reportUtils;

    @Autowired
    private ReportService reportService;

    @ParameterizedTest
    @MethodSource("answersProvider")
    void makeReport(List<Answer> answers) {

        var student = new Student("Test", "Testov");
        var report = StreamEx.of(answers)
                .map(Answer::getAnswerText)
                .prepend(student.getFirstName() + ' ' + student.getLastName())
                .toList();

        when(reportUtils.reportStream(student, answers))
                .thenReturn(StreamEx.of(report));

        reportService.makeReport(student, answers);

        verify(reportUtils).reportStream(student, answers);
        var inOrder = inOrder(ioService);

        report.forEach(s -> inOrder.verify(ioService).writeLine(s));
        inOrder.verifyNoMoreInteractions();

    }

    private static Stream<Arguments> answersProvider() {
        return StreamEx.of(
                Arguments.of(
                    List.of(
                        new Answer(new Question("1", "single", "one"),
                            "one")
                    )
                ),
                Arguments.of(List.of()),
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
                        )
                )
        );
    }

}