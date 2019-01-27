package ru.otus.springframework.library.books;

import lombok.*;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.genres.Genre;

import javax.persistence.*;
import java.util.Set;

import static one.util.streamex.StreamEx.of;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
public class Book {
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
    private @NonNull Set<Author> authors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "BOOK_TO_GENRE",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "GENRE_ID")
    )
    private @NonNull Set<Genre> genres;

    @OneToMany(mappedBy = "bookId", fetch = FetchType.EAGER)
    private @NonNull Set<Comment> comments;

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

    public String getCommentString() {
        return of(comments)
                .sortedBy(Comment::getCreated)
                .map(Comment::getText)
                .joining(System.lineSeparator() + "----" + System.lineSeparator());
    }
}
