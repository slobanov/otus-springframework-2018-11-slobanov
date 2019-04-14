package ru.otus.springframework.library.dao.reactive;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.mongodb.mongock.MongockConfig;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.of;

@DataMongoTest
@ActiveProfiles({"test", "test-reactive-mongodb"})
@Import({ReactiveSequenceRepository.class, MongockConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReactiveAuthorMongodbRepositoryTest {

    @Autowired
    private ReactiveAuthorMongodbRepository authorRepository;

    @Test
    void findAll() {
        var authors = authorRepository.findAll().collectList();

        StepVerifier.create(authors)
                .assertNext(as -> {
                    assertThat(as, hasSize(5));
                    assertThat(
                            StreamEx.of(as).map(Author::getFirstName).toList(),
                            contains("fName1", "fName2", "fName3", "fName4", "fName4")
                    );
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void findById(Long id, Optional<Author> expectedAuthor) {
        var author = authorRepository.findById(id);

        if (expectedAuthor.isPresent()) {
            StepVerifier.create(author)
                    .assertNext(a -> assertThat(a, equalTo(expectedAuthor.get())))
                    .verifyComplete();
        } else {
            StepVerifier.create(author).verifyComplete();
        }
    }

    private static Stream<Arguments> authorProvider() {
        return EntryStream.of(
                1L, new Author(1L, "fName1", "lName1"),
                2L, new Author(2L, "fName2", "lName1"),
                4L, new Author(4L, "fName4", "lName4"),
                42L, null
        ).mapValues(Optional::ofNullable).mapToValue((id, a) -> of(id, a)).values();
    }

    @Test
    void save() {
        var firstName = "Test";
        var lastName = "Testov";

        var author = authorRepository.saveObj(new Author("Test", "Testov"));

        StepVerifier.create(author)
                .assertNext(a -> {
                    assertThat(a.getFirstName(), equalTo(firstName));
                    assertThat(a.getLastName(), equalTo(lastName));
                })
                .verifyComplete();

        var allAuthors = authorRepository.findAll().collectList();
        StepVerifier.create(allAuthors)
                .assertNext(as -> {
                    assertThat(as, hasSize(6));
                    assertThat(
                            StreamEx.of(as).findAny(a -> a.getFirstName().equals("Test")).isPresent(),
                            equalTo(true)
                    );
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("authorProvider")
    void deleteById(Long id, Optional<Author> expectedAuthor) {
        var author = authorRepository.deleteByObjId(id);

        if (expectedAuthor.isPresent()) {
            var eAuthor = expectedAuthor.get();
            StepVerifier.create(author)
                    .assertNext(a -> assertThat(a, equalTo(eAuthor)))
                    .verifyComplete();

            var allAuthors = authorRepository.findAll().collectList();
            StepVerifier.create(allAuthors)
                    .assertNext(as -> {
                        assertThat(as, hasSize(4));
                        assertThat(
                                StreamEx.of(as).findAny(a -> a.getId().equals(eAuthor.getId())).isEmpty(),
                                equalTo(true)
                        );
                    })
                    .verifyComplete();

        } else {
            StepVerifier.create(author).verifyComplete();
        }
    }

}