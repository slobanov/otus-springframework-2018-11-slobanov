package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.springframework.stereotype.Service;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;

import java.util.List;

@Service
class ReportUtils {
    private final IOReportMessages ioReportMessages;

    ReportUtils(IOReportMessages ioReportMessages) {
        this.ioReportMessages = ioReportMessages;
    }

    StreamEx<String> reportStream(Student student, List<Answer> answers) {
        return StreamEx.of(answers).map(this::singleAnswerReport)
                .prepend(ioReportMessages.formatHeader(student))
                .append(statReport(answers));
    }

    private String statReport(List<Answer> answers) {
        var correctCount = StreamEx.of(answers).filter(Answer::isCorrect).toList().size();
        var totalCount = answers.size();
        return ioReportMessages.formatResult(correctCount, totalCount);
    }

    private String singleAnswerReport(Answer answer) {
        return ioReportMessages.formatAnswer(answer);
    }

}


