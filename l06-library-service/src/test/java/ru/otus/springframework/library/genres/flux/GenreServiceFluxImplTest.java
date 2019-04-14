package ru.otus.springframework.library.genres.flux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.dao.reactive.ReactiveGenreMongodbRepository;
import ru.otus.springframework.library.genres.Genre;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceFluxImplTest {

    @Mock
    private ReactiveGenreMongodbRepository genreRepository;

    @InjectMocks
    private GenreServiceFluxImpl genreService;

    @Test
    void all() {
        var expectedGenre = mock(Genre.class);
        when(genreRepository.findAll()).thenReturn(Flux.just(expectedGenre));

        StepVerifier.create(genreService.all())
                .assertNext(genre -> assertThat(genre, equalTo(expectedGenre)))
                .verifyComplete();

        verify(genreRepository).findAll();
    }

    @Test
    void newGenre() {
        var genreString = "genre";
        when(genreRepository.saveObj(any(Genre.class)))
                .thenReturn(Mono.just(new Genre((genreString))));
        when(genreRepository.findByName(genreString)).thenReturn(Mono.empty());

        StepVerifier.create(genreService.newGenre(genreString))
                .assertNext(genre -> assertThat(genre.getName(), equalTo(genreString)))
                .verifyComplete();

        verifyGenreSaveObj(genreString);
    }

    @Test
    void newGenreDuplicate() {
        var genreString = "genre";
        var genreObj = new Genre(genreString);
        when(genreRepository.saveObj(any(Genre.class)))
                .thenReturn(Mono.just(genreObj));
        when(genreRepository.findByName(genreString)).thenReturn(Mono.just(genreObj));

        StepVerifier.create(genreService.newGenre(genreString))
                .expectError(IllegalArgumentException.class)
                .verify();

        verifyGenreSaveObj(genreString);
    }

    private void verifyGenreSaveObj(String genreString) {
        var genreCaptor = ArgumentCaptor.forClass(Genre.class);
        verify(genreRepository).saveObj(genreCaptor.capture());
        assertThat(genreCaptor.getValue().getName(), equalTo(genreString));

    }

    @Test
    void removeGenre() {
        var genreString = "genre";
        var id = 1L;
        var expectedGenre = new Genre(id, genreString);
        when(genreRepository.deleteByObjId(id)).thenReturn(Mono.just(expectedGenre));
        when(genreRepository.findByName(genreString)).thenReturn(Mono.just(expectedGenre));

        StepVerifier.create(genreService.removeGenre(genreString))
                .assertNext(genre -> assertThat(genre, equalTo(expectedGenre)))
                .verifyComplete();

        verify(genreRepository).deleteByObjId(id);
        verify(genreRepository).findByName(genreString);
    }

    @Test
    void removeGenreEmpty() {
        var genreString = "genre";
        when(genreRepository.findByName(genreString)).thenReturn(Mono.empty());

        StepVerifier.create(genreService.removeGenre(genreString)).verifyComplete();
        verify(genreRepository).findByName(genreString);
    }
}