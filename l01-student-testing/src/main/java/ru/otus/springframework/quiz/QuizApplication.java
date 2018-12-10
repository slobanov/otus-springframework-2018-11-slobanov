package ru.otus.springframework.quiz;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import ru.otus.springframework.quiz.answer.AnswerService;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.question.QuestionService;
import ru.otus.springframework.quiz.report.ReportService;

@Slf4j
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
