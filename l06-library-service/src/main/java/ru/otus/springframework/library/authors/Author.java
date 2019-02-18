package ru.otus.springframework.library.authors;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.*;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Document
public class Author {
    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NonNull String firstName;
    private @NonNull String lastName;

    public String displayName() {
        return format("%s %s [%s]", firstName, lastName, id);
    }
}
