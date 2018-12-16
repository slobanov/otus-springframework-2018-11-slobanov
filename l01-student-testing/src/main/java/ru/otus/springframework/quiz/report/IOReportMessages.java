package ru.otus.springframework.quiz.report;

import ru.otus.springframework.quiz.answer.Answer;
import ru.otus.springframework.quiz.auth.Student;

import java.util.Map;

import static java.lang.String.format;
import static java.lang.System.lineSeparator;

public class IOReportMessages {
    private final String headerText;
    private final String resultText;
    private final String actualText;
    private final String expectedText;
    private final String correctText;
    private final String incorrectText;

    public IOReportMessages(Map<String, String> reportMessageMap) {
        this.headerText = reportMessageMap.get("headerText");
        this.resultText = reportMessageMap.get("resultText");
        this.actualText = reportMessageMap.get("actualText");
        this.expectedText = reportMessageMap.get("expectedText");
        this.correctText = reportMessageMap.get("correctText");
        this.incorrectText = reportMessageMap.get("incorrectText");
    }

    String formatHeader(Student student) {
        return format(
                "%s: %s %s",
                headerText,
                student.getFirstName(),
                student.getLastName()
        ) + lineSeparator();
    }

    String formatAnswer(Answer answer) {
        var question = answer.getQuestion();

        return format("%s: %s", question.getName(), question.getText())
             + lineSeparator()
             + format(
                "%s: %s, %s: %s, %s",
                actualText,
                answer.getAnswerText(),
                expectedText,
                question.getAnswer(),
                answer.isCorrect() ? correctText : incorrectText
               )
            + lineSeparator();
    }

    String formatResult(int correctCnt, int totalCount) {
        return format(
                "%s: %s/%s",
                resultText,
                correctCnt,
                totalCount
               );
    }


}
