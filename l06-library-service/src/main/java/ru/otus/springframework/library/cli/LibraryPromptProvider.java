package ru.otus.springframework.library.cli;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Service;

@Service
class LibraryPromptProvider implements PromptProvider {
    private final String prompt;

    LibraryPromptProvider(@Value("${library.prompt}") String promptStr) {
        this.prompt = promptStr;
    }

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(prompt, AttributedStyle.DEFAULT.foreground(3));
    }
}
