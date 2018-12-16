package ru.otus.springframework.quiz.question;

import lombok.Data;
import lombok.NonNull;

@Data
public class Question {
    private final @NonNull String name;
    private final @NonNull String text;
    private final @NonNull String answer;
}
