package ru.otus.springframework.quiz.answer;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.Question;

import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class IOAnswerServiceTest {

    @MockBean
    private IOService ioService;

    @Autowired
    private AnswerService answerService;

    @ParameterizedTest
    @MethodSource("questionDataProvider")
    void reply(Question question) {
        var answerText = question.getAnswer();
        when(ioService.ask(question.getText())).thenReturn(answerText);

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