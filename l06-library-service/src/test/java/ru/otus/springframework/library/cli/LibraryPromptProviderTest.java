package ru.otus.springframework.library.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class LibraryPromptProviderTest {
    @ParameterizedTest
    @ValueSource(strings = {"123", "dsa dsa:>", ""})
    void getPrompt(String prompt) {
        var quizPromptProvider = new LibraryPromptProvider(prompt);
        assertThat(quizPromptProvider.getPrompt().toString(), equalTo(prompt));
    }

}