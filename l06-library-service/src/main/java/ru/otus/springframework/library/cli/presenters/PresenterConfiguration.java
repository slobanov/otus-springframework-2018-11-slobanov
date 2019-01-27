package ru.otus.springframework.library.cli.presenters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.genres.Genre;

import static one.util.streamex.EntryStream.of;

@Configuration
class PresenterConfiguration {

    @Bean
    Presenter<Book> bookPresenter() {
        return new PresenterImpl<>(
                Book.class,
                () -> of(
                        "isbn", "isbn",
                        "title", "title",
                        "authorString", "authors",
                        "genreString", "genres",
                        "commentString", "comments"
                )
        );
    }

    @Bean
    Presenter<Author> authorPresenter() {
        return new PresenterImpl<>(
                Author.class,
                () -> of(
                        "id", "id",
                        "firstName", "firstName",
                        "lastName", "lastName"
                )
        );
    }

    @Bean
    Presenter<Genre> genrePresenter() {
        return new PresenterImpl<>(
                Genre.class,
                () -> of("name", "name")
        );
    }

    @Bean
    Presenter<Comment> commentPresenter() {
        return new PresenterImpl<>(
                Comment.class,
                () -> of(
                        "prettyDate", "created",
                        "text", "text"
                )
        );
    }

}
