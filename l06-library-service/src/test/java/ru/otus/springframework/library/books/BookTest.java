package ru.otus.springframework.library.books;

import org.junit.jupiter.api.Test;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.genres.Genre;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookTest {

    @Test
    void getAuthorString() {
        var author1 = mockAuthor("author1");
        var author2 = mockAuthor("author2");

        var book = new Book("", "", List.of(author1, author2), List.of());
        assertThat(book.getAuthorString(), containsString("author1")) ;
        assertThat(book.getAuthorString(), containsString("author2")) ;
    }

    private Author mockAuthor(String displayName) {
        var author = mock(Author.class);
        when(author.displayName()).thenReturn(displayName);
        return author;
    }

    @Test
    void getGenreString() {
        var genre1 = mockGenre("genre1");
        var genre2 = mockGenre("genre2");

        var book = new Book("", "", List.of(), List.of(genre1, genre2));
        assertThat(book.getGenreString(), containsString("genre1"));
        assertThat(book.getGenreString(), containsString("genre2")) ;
    }

    private Genre mockGenre(String name) {
        var genre = mock(Genre.class);
        when(genre.getName()).thenReturn(name);
        return genre;
    }
}