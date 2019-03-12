package ru.otus.springframework.library.books;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.genres.Genre;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
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

    public Set<Genre> getGenres() {
        if (genres == null) {
            genres = new HashSet<>();
        }
        genres = filterNulls(genres);
        return genres;
    }

    public Set<Author> getAuthors() {
        if (authors == null) {
            authors = new HashSet<>();
        }
        authors = filterNulls(authors);
        return authors;
    }

    public String getAuthorString() {
        return of(sortedAuthors())
                .map(Author::displayName)
                .joining(", ");
    }

    public String getGenreString() {
        return of(sortedGenres())
                .map(Genre::getName).joining(", ");
    }

    public List<Author> sortedAuthors() {
        return of(getAuthors())
                .sortedBy(Author::getId)
                .toList();
    }

    public List<Genre> sortedGenres() {
        return of(getGenres())
                .sortedBy(Genre::getId)
                .toList();
    }

    private static <T> Set<T> filterNulls(Set<T> set) {
        return of(set).nonNull().collect(toCollection(HashSet::new));
    }

}
