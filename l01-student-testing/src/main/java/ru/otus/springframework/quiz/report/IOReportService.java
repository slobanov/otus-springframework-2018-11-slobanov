package ru.otus.springframework.quiz.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;
import ru.otus.springframework.quiz.io.IOService;

import java.util.List;

@Service
@Profile("!shell")
@Slf4j
class IOReportService implements ReportService {

    private final IOService ioService;
    private final ReportUtils reportUtils;

    IOReportService(IOService ioService, ReportUtils reportUtils) {
        this.ioService = ioService;
        this.reportUtils = reportUtils;
    }

    @Override
    public void makeReport(Student student, List<Answer> answers) {
        log.debug("report for student = {}; answers = {}", student, answers);
        reportUtils.reportStream(student, answers).forEach(ioService::writeLine);
    }

}
