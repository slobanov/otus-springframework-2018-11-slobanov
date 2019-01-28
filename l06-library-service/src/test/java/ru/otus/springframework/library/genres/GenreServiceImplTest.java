package ru.otus.springframework.library.genres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    private SimpleDAO<Genre> genreDAO;

    private GenreService genreService;

    @BeforeEach
    void init() {
        genreDAO = (SimpleDAO<Genre>) mock(SimpleDAO.class);
        genreService = new GenreServiceImpl(genreDAO);
    }

    @Test
    void all() {
        genreService.all();
        verify(genreDAO).fetchAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void newGenre(String genre) {
        var genreObj = new Genre(genre);
        when(genreDAO.save(genreObj)).thenReturn(genreObj);

        var resGenre = genreService.newGenre(genre);
        verify(genreDAO).save(genreObj);
        assertThat(resGenre, equalTo(genreObj));
    }

    @Test
    void newGenreDuplicate() {
        var genre = "genre";
        when(genreDAO.findByField("NAME", genre)).thenReturn(List.of(mock(Genre.class)));

        assertThrows(IllegalArgumentException.class, () -> genreService.newGenre(genre));
        verify(genreDAO).findByField("NAME", genre);
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void removeGenre(String genre) {
        var genreObj = new Genre(42L, genre);
        when(genreDAO.findByField("NAME", genre)).thenReturn(List.of(genreObj));
        when(genreDAO.deleteById(anyLong())).thenReturn(Optional.of(genreObj));

        var resGenre = genreService.removeGenre(genre);

        assertThat(resGenre.isPresent(), equalTo(true));
        assertThat(resGenre.get(), equalTo(genreObj));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qwe", "yy"})
    void removeGenreFail(String genre) {
        var id = 42L;
        var genreObj = new Genre(id, genre);
        when(genreDAO.findByField("NAME", genre)).thenReturn(List.of(genreObj));
        when(genreDAO.deleteById(anyLong())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(IllegalArgumentException.class, () -> genreService.removeGenre(genre));
        verify(genreDAO).findByField("NAME", genre);
        verify(genreDAO).deleteById(id);

    }

}