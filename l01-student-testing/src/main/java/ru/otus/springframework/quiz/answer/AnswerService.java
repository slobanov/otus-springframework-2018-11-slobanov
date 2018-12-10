package ru.otus.springframework.quiz.answer;

import ru.otus.springframework.quiz.question.Question;

public interface AnswerService {
    Answer reply(Question question);
}
