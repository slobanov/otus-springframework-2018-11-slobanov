package ru.otus.springframework.quiz;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) {
        try(
            var context = new ClassPathXmlApplicationContext("/spring-context.xml")
        ) {
            var quizApp = context.getBean(QuizApplication.class);
            quizApp.performQuiz();
        }
    }

}

