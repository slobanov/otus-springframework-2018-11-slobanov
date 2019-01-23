package ru.otus.springframework.library.books;

import lombok.*;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;

import static one.util.streamex.StreamEx.of;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Book {
    private Long id;
    private final @NonNull String isbn;
    private final @NonNull String title;
    private final @NonNull List<Author> authors;
    private final @NonNull List<Genre> genres;

    public String getAuthorString() {
        return of(authors).map(Author::displayName).joining(", ");
    }

    public String getGenreString() {
        return of(genres).map(Genre::getName).joining(", ");
    }
}
