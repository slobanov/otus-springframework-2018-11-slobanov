package ru.otus.springframework.library.controllers.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.CommentService;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(CommentsRestController.class)
@ActiveProfiles("rest")
class CommentsRestControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mvc);
    }


    @Test
    void comments() {
        var isbn = "qwe";
        var comment = new Comment(
                1L,
                new Book(
                        42L,
                        isbn,
                        "title",
                        Set.of(),
                        Set.of()
                ),
                "123",
                new Date()
        );
        var comments = List.of(comment);
        when(commentService.commentsFor(isbn)).thenReturn(comments);

        var commentsRequest = RestAssuredMockMvc.get("/api/v2/comments/" + isbn);

        commentsRequest.then().statusCode(200);
        verify(commentService).commentsFor(isbn);
        assertThat(asList(commentsRequest.as(Comment[].class)), equalTo(comments));
    }
}