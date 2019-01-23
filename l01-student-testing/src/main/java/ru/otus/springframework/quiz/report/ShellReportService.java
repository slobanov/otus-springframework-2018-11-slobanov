package ru.otus.springframework.quiz.report;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Profile("shell")
public class ShellReportService implements ReportService {

    private List<String> report = Collections.emptyList();

    private final ReportUtils reportUtils;

    ShellReportService(ReportUtils reportUtils) {
        this.reportUtils = reportUtils;
    }

    @Override
    public void makeReport(Student student, List<Answer> answers) {
        report = reportUtils.reportStream(student, answers).toList();
    }

    public List<String> getReport() {
        return new ArrayList<>(report);
    }
}
