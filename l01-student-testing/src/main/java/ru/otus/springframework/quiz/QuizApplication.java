package ru.otus.springframework.quiz;

import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import ru.otus.springframework.quiz.answer.AnswerService;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.question.QuestionService;
import ru.otus.springframework.quiz.report.ReportService;

@Service
class QuizApplication {
    private final QuestionService questionService;
    private final AuthService authService;
    private final AnswerService answerService;
    private final ReportService reportService;

    QuizApplication(
            QuestionService questionService,
            AuthService authService,
            AnswerService answerService,
            ReportService reportService
    ) {
        this.questionService = questionService;
        this.authService = authService;
        this.answerService = answerService;
        this.reportService = reportService;
    }

    void performQuiz() {
        var student = authService.authorize();

        var questions = questionService.allQuestions();
        var answers = StreamEx.of(questions).map(answerService::reply).toList();

        reportService.makeReport(student, answers);
    }

}
