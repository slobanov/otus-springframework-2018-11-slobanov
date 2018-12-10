package ru.otus.springframework.quiz.report;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.io.IOService;

import java.util.List;

@Slf4j
class IOReportService implements ReportService {

    private final IOService ioService;
    private final IOReportMessages ioReportMessages;

    IOReportService(IOService ioService, IOReportMessages ioReportMessages) {
        this.ioService = ioService;
        this.ioReportMessages = ioReportMessages;
    }

    @Override
    public void makeReport(Student student, List<Answer> answers) {
        log.debug("report for student = {}; answers = {}", student, answers);
        ioService.writeLine(ioReportMessages.formatHeader(student));

        answers.forEach(this::singleAnswerReport);
        statReport(answers);
    }

    private void singleAnswerReport(Answer answer) {
        ioService.writeLine(ioReportMessages.formatAnswer(answer));
    }

    private void statReport(List<Answer> answers) {
        var correctCount = StreamEx.of(answers).filter(Answer::isCorrect).toList().size();
        var totalCount = answers.size();
        ioService.writeLine(ioReportMessages.formatResult(correctCount, totalCount));
    }
}
