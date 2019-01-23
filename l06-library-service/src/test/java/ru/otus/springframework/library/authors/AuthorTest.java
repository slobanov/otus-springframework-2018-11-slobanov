package ru.otus.springframework.library.authors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

class AuthorTest {

    @ParameterizedTest
    @CsvSource({
            "1,fName,lName",
            "42,wqe,123 456"
    })
    void displayName(Long id, String firstName, String lastName) {
        var author = new Author(id, firstName, lastName);
        assertThat(author.displayName(), containsString(firstName));
        assertThat(author.displayName(), containsString(lastName));
        assertThat(author.displayName(), containsString("[" + id + "]"));
    }
}