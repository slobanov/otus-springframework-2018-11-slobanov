package ru.otus.springframework.quiz.question;

import java.util.List;

public interface QuestionDAO {
    List<Question> readAll();
}
