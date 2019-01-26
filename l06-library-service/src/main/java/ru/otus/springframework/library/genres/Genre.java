package ru.otus.springframework.library.genres;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Genre {
    private Long id;
    private final @NonNull String name;
}
