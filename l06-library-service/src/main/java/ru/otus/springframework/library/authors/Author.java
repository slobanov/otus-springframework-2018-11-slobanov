package ru.otus.springframework.library.authors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Author {
    private Long id;
    private final @NonNull String firstName;
    private final @NonNull String lastName;

    public String displayName() {
        return format("%s %s [%s]", firstName, lastName, id);
    }
}
