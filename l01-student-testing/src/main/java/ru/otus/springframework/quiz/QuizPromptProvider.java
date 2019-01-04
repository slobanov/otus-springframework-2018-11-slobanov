package ru.otus.springframework.quiz;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Service;

@Service
@Profile("shell")
class QuizPromptProvider implements PromptProvider {
    private final String prompt;

    QuizPromptProvider(@Value("${quiz.prompt}") String promptStr) {
        this.prompt = promptStr;
    }

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(prompt, AttributedStyle.DEFAULT.foreground(3));
    }
}
