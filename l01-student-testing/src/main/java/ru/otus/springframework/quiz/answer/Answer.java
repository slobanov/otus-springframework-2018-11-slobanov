package ru.otus.springframework.quiz.answer;

import lombok.Data;
import lombok.NonNull;
import ru.otus.springframework.quiz.question.Question;

@Data
public class Answer {
    private final @NonNull Question question;
    private final @NonNull String answerText;

    public boolean isCorrect() {
        return question.getAnswer().equals(answerText);
    }
}
