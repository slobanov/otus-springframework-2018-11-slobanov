package ru.otus.springframework.quiz.answer;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.Question;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IOAnswerServiceTest {

    @ParameterizedTest
    @MethodSource("questionDataProvider")
    void reply(Question question) {
        var answerText = question.getAnswer();

        var ioService = mock(IOService.class);
        when(ioService.ask(question.getText())).thenReturn(answerText);

        var answerService = new IOAnswerService(ioService);

        assertThat(
                answerService.reply(question),
                equalTo(new Answer(question, answerText))
        );
    }

    private static Stream<Arguments> questionDataProvider() {
        return StreamEx.of(
                new Question("1", "single", "one"),
                new Question(
                        "name1",
                        "long," + lineSeparator() + "multi line text", ",,"
                ),
                new Question("name2", "other text", "new answer")
        ).map(Arguments::of);
    }
}