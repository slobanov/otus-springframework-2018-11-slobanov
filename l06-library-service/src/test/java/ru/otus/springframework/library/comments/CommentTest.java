package ru.otus.springframework.library.comments;

import one.util.streamex.StreamEx;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.of;

class CommentTest {

    @ParameterizedTest
    @MethodSource("commentsProvider")
    void getPrettyDate(Date dt, String prettyDate) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        var comment = new Comment(1L, 1L, "", dt);
        assertThat(comment.getPrettyDate(), equalTo(prettyDate));
        
    }

    private static Stream<Arguments> commentsProvider() {
        return StreamEx.of(
                of(new Date(10000L), "1970-01-01 00:00:10"),
                of(new Date(0L), "1970-01-01 00:00:00")
        );
    }
}