package ru.otus.springframework.library.books;

import org.junit.jupiter.api.Test;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.genres.Genre;

import java.util.Date;
import java.util.Set;

import static java.lang.System.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookTest {

    @Test
    void getAuthorString() {
        var author1 = mockAuthor("author1");
        var author2 = mockAuthor("author2");

        var book = new Book("", "", Set.of(author1, author2), Set.of(), Set.of());
        assertThat(book.getAuthorString(), containsString("author1")) ;
        assertThat(book.getAuthorString(), containsString("author2")) ;
    }

    private static Author mockAuthor(String displayName) {
        var author = mock(Author.class);
        when(author.displayName()).thenReturn(displayName);
        return author;
    }

    @Test
    void getGenreString() {
        var genre1 = mockGenre("genre1");
        var genre2 = mockGenre("genre2");

        var book = new Book("", "", Set.of(), Set.of(genre1, genre2), Set.of());
        assertThat(book.getGenreString(), containsString("genre1"));
        assertThat(book.getGenreString(), containsString("genre2"));
    }

    private static Genre mockGenre(String name) {
        var genre = mock(Genre.class);
        when(genre.getName()).thenReturn(name);
        return genre;
    }

    @Test
    void getCommentString() {
        var comment1 = mockComment("123", 10L);
        var comment2 = mockComment("qwe", 5L);

        var book = new Book("", "", Set.of(), Set.of(), Set.of(comment1, comment2));

        assertThat(book.getCommentString(), containsString("123"));
        assertThat(book.getCommentString(), containsString("qwe"));

        assertThat(book.getCommentString(), equalTo(
                "qwe" + lineSeparator() + "----" + lineSeparator() + "123"
        ));
    }

    private static Comment mockComment(String text, Long offset) {
        var comment = mock(Comment.class);
        when(comment.getText()).thenReturn(text);
        when(comment.getCreated()).thenReturn(new Date(offset));
        return comment;
    }
}