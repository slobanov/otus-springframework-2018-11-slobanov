package ru.otus.springframework.library.genres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import ru.otus.springframework.library.dao.GenreDAO;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    private GenreDAO genreDAO;

    private GenreService genreService;

    @BeforeEach
    void init() {
        genreDAO = mock(GenreDAO.class);
        genreService = new GenreServiceImpl(genreDAO);
    }

    @Test
    void all() {
        genreService.all();
        verify(genreDAO).findAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void newGenre(String genre) {
        var genreObj = new Genre(genre);
        when(genreDAO.saveObj(genreObj)).thenReturn(genreObj);

        var resGenre = genreService.newGenre(genre);
        verify(genreDAO).saveObj(genreObj);
        assertThat(resGenre, equalTo(genreObj));
    }

    @Test
    void newGenreDuplicate() {
        var genre = "genre";
        when(genreDAO.findByName(genre)).thenReturn(Optional.of(mock(Genre.class)));

        assertThrows(IllegalArgumentException.class, () -> genreService.newGenre(genre));
        verify(genreDAO).findByName(genre);
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void removeGenre(String genre) {
        var genreObj = new Genre(42L, genre);
        when(genreDAO.findByName(genre)).thenReturn(Optional.of(genreObj));
        when(genreDAO.deleteByObjId(anyLong())).thenReturn(Optional.of(genreObj));

        var resGenre = genreService.removeGenre(genre);

        assertThat(resGenre.isPresent(), equalTo(true));
        assertThat(resGenre.get(), equalTo(genreObj));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void removeGenreFail(String genre) {
        var id = 42L;
        var genreObj = new Genre(id, genre);
        when(genreDAO.findByName(genre)).thenReturn(Optional.of(genreObj));
        when(genreDAO.deleteByObjId(anyLong())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(IllegalArgumentException.class, () -> genreService.removeGenre(genre));
        verify(genreDAO).findByName(genre);
        verify(genreDAO).deleteByObjId(id);

    }

}