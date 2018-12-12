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

class QuestionCSVTest {

    @ParameterizedTest
    @MethodSource("questionDataProvider")
    void readAll(String filePath, List<Question> questions) {
        var questionDAO = new QuestionCSV(filePath);
        assertThat(questionDAO.readAll(), equalTo(questions));
    }

    private static Stream<Arguments> questionDataProvider() {
        return StreamEx.of(
                Arguments.of("q1.csv", List.of(new Question("1", "single", "one"))),
                Arguments.of("q2.csv", List.of()),
                Arguments.of("q3.csv", List.of(
                        new Question(
                                "name1",
                                "long,"+lineSeparator()+"multi line text", ",,"
                        ),
                        new Question("name2", "other text", "new answer")
                ))
        );
    }
}