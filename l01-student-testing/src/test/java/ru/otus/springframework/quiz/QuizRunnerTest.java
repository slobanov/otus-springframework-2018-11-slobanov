package ru.otus.springframework.quiz;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.verify;

@SpringBootTest
class QuizRunnerTest {

    @MockBean
    private QuizApplication quizApplication;

    @Test
    void run() {
        verify(quizApplication).performQuiz();
    }
}