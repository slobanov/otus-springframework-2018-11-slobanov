package ru.otus.springframework.quiz.question;

import java.util.List;

interface QuestionDAO {
    List<Question> readAll();
}
