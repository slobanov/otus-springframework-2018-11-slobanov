package ru.otus.springframework.quiz.question;

import lombok.Data;

@Data
public class Question {
    private final String name;
    private final String text;
    private final String answer;
}
