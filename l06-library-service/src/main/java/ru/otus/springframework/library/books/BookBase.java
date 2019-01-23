package ru.otus.springframework.library.books;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class BookBase {
    private Long id;
    private final @NonNull String isbn;
    private final @NonNull String title;
}
