package ru.otus.springframework.quiz.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Profile("shell")
public class ShellAuthService implements AuthService {

    private Optional<Student> student = Optional.empty();

    public void useAuthData(String firstName, String lastName) {
        student = Optional.of(new Student(firstName, lastName));
    }

    @Override
    public Student authorize() {
        return student.orElseThrow(
                () -> new IllegalStateException("You need to provide data first")
        );
    }
}
