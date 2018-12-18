package ru.otus.springframework.quiz;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        try(
            var context = new AnnotationConfigApplicationContext(QuizApplicationConfiguration.class)
        ) {
            var quizApp = context.getBean(QuizApplication.class);
            quizApp.performQuiz();
        }
    }
}

