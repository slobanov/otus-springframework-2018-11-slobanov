package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.Question;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class IOReportServiceTest {

    @ParameterizedTest
    @MethodSource("answersProvider")
    void makeReport(List<Answer> answers, int correct) {
        var ioService = mock(IOService.class);
        var ioReportMessages = mock(IOReportMessages.class);

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

        var reportService = new IOReportService(ioService, ioReportMessages);

        reportService.makeReport(student, answers);

        var inOrder = inOrder(ioService);

        inOrder.verify(ioService).writeLine(studentRepr);
        answers.forEach(a -> inOrder.verify(ioService).writeLine(a.getAnswerText()));
        inOrder.verify(ioService).writeLine(resultRepr);
        inOrder.verifyNoMoreInteractions();
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