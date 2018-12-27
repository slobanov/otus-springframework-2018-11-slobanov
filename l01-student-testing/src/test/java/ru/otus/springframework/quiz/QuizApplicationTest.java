package ru.otus.springframework.quiz;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.answer.AnswerService;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.question.Question;
import ru.otus.springframework.quiz.question.QuestionService;
import ru.otus.springframework.quiz.report.ReportService;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class QuizApplicationTest {

    @MockBean
    private QuestionService questionService;

    @MockBean
    private AuthService authService;

    @MockBean
    private ReportService reportService;

    @MockBean
    private AnswerService answerService;

    @Autowired
    private QuizApplication quizApplication;

    private List<Question> initQuestionService() {
        var questions = List.of(mock(Question.class), mock(Question.class));
        when(questionService.allQuestions()).thenReturn(questions);
        return questions;
    }

    private Student initAuthService() {
        var student = mock(Student.class);
        when(authService.authorize()).thenReturn(student);
        return student;
    }

    private List<Answer> initAnswerService(List<Question> questions) {
        var answers = StreamEx.of(questions).map(q -> new Answer(q, "")).toList();
        answers.forEach(a -> when(answerService.reply(a.getQuestion())).thenReturn(a));
        return answers;
    }

    @Test
    void performQuiz() {
        var questions = initQuestionService();
        var student = initAuthService();
        var answers = initAnswerService(questions);

        var inOrder = inOrder(
                questionService,
                answerService,
                authService,
                reportService
        );
        quizApplication.performQuiz();

        inOrder.verify(authService).authorize();
        inOrder.verify(questionService).allQuestions();
        questions.forEach(q -> inOrder.verify(answerService).reply(q));
        inOrder.verify(reportService).makeReport(student, answers);
        inOrder.verifyNoMoreInteractions();

    }
}