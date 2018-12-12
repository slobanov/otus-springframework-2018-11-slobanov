package ru.otus.springframework.quiz.question;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuestionServiceImplTest {

    @ParameterizedTest
    @MethodSource("questionDataProvider")
    void allQuestions(List<Question> questions) {
        var questionDAO = mock(QuestionDAO.class);
        when(questionDAO.readAll()).thenReturn(questions);

        var questionService = new QuestionServiceImpl(questionDAO);
        assertThat(questionService.allQuestions(), equalTo(questions));
    }


    private static Stream<Arguments> questionDataProvider() {
        return StreamEx.of(
                List.of(new Question("1", "single", "one")),
                List.of(),
                List.of(
                        new Question(
                                "name1",
                                "long,"+lineSeparator()+"multi line text", ",,"
                        ),
                        new Question("name2", "other text", "new answer")
                )
        ).map(Arguments::of);
    }
}