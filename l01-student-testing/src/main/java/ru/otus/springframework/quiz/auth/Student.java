package ru.otus.springframework.quiz.auth;

import lombok.Data;
import lombok.NonNull;

@Data
public class Student {
    private final @NonNull String firstName;
    private final @NonNull String lastName;
}
