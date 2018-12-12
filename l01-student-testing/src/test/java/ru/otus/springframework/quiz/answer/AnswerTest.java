package ru.otus.springframework.quiz.answer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.springframework.quiz.question.Question;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AnswerTest {

    @ParameterizedTest
    @ValueSource(strings = {"answer1", "some other answer"})
    void correct(String answerText) {
        var question = mock(Question.class);
        when(question.getAnswer()).thenReturn(answerText);

        var answer = new Answer(question, answerText);

        assertTrue(answer.isCorrect(), "answer is correct");
    }

    @ParameterizedTest
    @CsvSource({
            "true answer, false answer",
            "some string, other string"
    })
    void Incorrect(String correctAnswer, String incorrectAnswer) {
        var question = mock(Question.class);
        when(question.getAnswer()).thenReturn(incorrectAnswer);

        var answer = new Answer(question, correctAnswer);

        assertFalse(answer.isCorrect(), "answer is incorrect");
    }
}