package ru.otus.springframework.quiz.answer;

import lombok.Data;
import ru.otus.springframework.quiz.question.Question;

@Data
public class Answer {
    private final Question question;
    private final String answerText;

    public boolean isCorrect() {
        return question.getAnswer().equals(answerText);
    }
}
