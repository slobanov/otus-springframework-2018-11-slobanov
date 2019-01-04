package ru.otus.springframework.quiz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("production")
class QuizRunner implements CommandLineRunner {

    private final QuizApplication quizApplication;

    QuizRunner(QuizApplication quizApplication) {
        this.quizApplication = quizApplication;
    }

    @Override
    public void run(String... args) {
        quizApplication.performQuiz();
    }
}
