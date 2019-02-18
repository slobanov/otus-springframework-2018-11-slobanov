package ru.otus.springframework.library.books;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.genres.Genre;

import javax.persistence.*;
import java.util.Set;

import static one.util.streamex.StreamEx.of;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Document
public class Book {
    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private @NonNull String isbn;
    private @NonNull String title;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "BOOK_TO_AUTHOR",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID")
    )
    @DBRef
    private @NonNull Set<Author> authors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "BOOK_TO_GENRE",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_ID")
    )
    @DBRef
    private @NonNull Set<Genre> genres;

    public String getAuthorString() {
        return of(authors)
                .sortedBy(Author::getId)
                .map(Author::displayName)
                .joining(", ");
    }

    public String getGenreString() {
        return of(genres)
                .sortedBy(Genre::getId)
                .map(Genre::getName).joining(", ");
    }

}
