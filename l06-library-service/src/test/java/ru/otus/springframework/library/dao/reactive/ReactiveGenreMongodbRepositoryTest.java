package ru.otus.springframework.library.dao.reactive;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.dao.mongodb.mongock.MongockConfig;
import ru.otus.springframework.library.genres.Genre;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataMongoTest
@ActiveProfiles({"test", "test-reactive-mongodb"})
@Import({ReactiveSequenceRepository.class, MongockConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReactiveGenreMongodbRepositoryTest {

    @Autowired
    private ReactiveGenreMongodbRepository genreRepository;


    @Test
    void findAll() {
        var genres = genreRepository.findAll().collectList();

        StepVerifier.create(genres)
                .assertNext(gs -> {
                    assertThat(gs, hasSize(3));
                    assertThat(
                            StreamEx.of(gs).map(Genre::getName).toList(),
                            contains("genre1", "genre2", "genre3")
                    );
                })
                .verifyComplete();
    }

    @Test
    void findByName() {
        var name = "genre2";
        var expectedGenre = new Genre(2L, name);

        StepVerifier.create(genreRepository.findByName(name))
                .assertNext(genre -> assertThat(genre, equalTo(expectedGenre)))
                .verifyComplete();
    }

    @Test
    void save() {
        var name = "new genre";

        var genre = genreRepository.saveObj(new Genre(name));

        StepVerifier.create(genre)
                .assertNext(g -> assertThat(g.getName(), equalTo(name)))
                .verifyComplete();

        var allGenres = genreRepository.findAll().collectList();
        StepVerifier.create(allGenres)
                .assertNext(genres -> {
                    assertThat(genres, hasSize(4));
                    assertThat(
                            StreamEx.of(genres).findAny(g -> g.getName().equals(name)).isPresent(),
                            equalTo(true)
                    );
                })
                .verifyComplete();
    }

    @Test
    void remove() {
        var id = 2L;
        var genre = genreRepository.deleteByObjId(id);

        StepVerifier.create(genre)
                .assertNext(g -> assertThat(g, equalTo(new Genre(2L, "genre2"))))
                .verifyComplete();

        var allGenres = genreRepository.findAll().collectList();
        StepVerifier.create(allGenres)
                .assertNext(gs -> {
                    assertThat(gs, hasSize(2));
                    assertThat(
                            StreamEx.of(gs).findAny(g -> g.getName().equals("genre2")).isEmpty(),
                            equalTo(true)
                    );
                })
                .verifyComplete();
    }





}