package ru.otus.springframework.quiz.report;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.quiz.auth.Student;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("shell")
class ShellReportServiceTest {

    @MockBean
    private ReportUtils reportUtils;

    @Autowired
    private ShellReportService shellReportService;

    private final Student student = new Student("1", "2");

    @BeforeEach
    void init() {
        when(reportUtils.reportStream(any(Student.class), anyList()))
                .thenReturn(StreamEx.of("1", "2", "3"));

        shellReportService.makeReport(student, Collections.emptyList());
    }

    @Test
    void makeReport() {
        verify(reportUtils).reportStream(student, Collections.emptyList());
    }

    @Test
    void getReport() {
        assertThat(shellReportService.getReport(), contains("1", "2", "3"));
    }
}