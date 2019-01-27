package ru.otus.springframework.library.authors;

import lombok.*;

import javax.persistence.*;

import static java.lang.String.format;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NonNull String firstName;
    private @NonNull String lastName;

    public String displayName() {
        return format("%s %s [%s]", firstName, lastName, id);
    }
}
