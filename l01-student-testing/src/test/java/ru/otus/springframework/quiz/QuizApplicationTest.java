package ru.otus.springframework.quiz;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.answer.AnswerService;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.question.Question;
import ru.otus.springframework.quiz.question.QuestionService;
import ru.otus.springframework.quiz.report.ReportService;

import java.util.List;

import static org.mockito.Mockito.*;

class QuizApplicationTest {

    @Test
    void performQuiz() {
        var questionService = mock(QuestionService.class);
        var questions = List.of(mock(Question.class), mock(Question.class));
        when(questionService.allQuestions()).thenReturn(questions);

        var student = mock(Student.class);
        var authService = mock(AuthService.class);
        when(authService.authorize()).thenReturn(student);

        var answerService = mock(AnswerService.class);
        var answers = StreamEx.of(questions).map(q -> new Answer(q, "")).toList();
        answers.forEach(a -> when(answerService.reply(a.getQuestion())).thenReturn(a));

        var reportService = mock(ReportService.class);


        var quizApplication = new QuizApplication(
                questionService,
                authService,
                answerService,
                reportService
        );

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