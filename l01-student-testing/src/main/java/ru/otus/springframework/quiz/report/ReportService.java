package ru.otus.springframework.quiz.report;

import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;

import java.util.List;

public interface ReportService {
    void makeReport(Student student, List<Answer> answers);
}
