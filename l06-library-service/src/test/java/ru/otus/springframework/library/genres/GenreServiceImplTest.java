package ru.otus.springframework.library.genres;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class GenreServiceImplTest {

    @MockBean
    private SimpleDAO<Genre> genreDAO;

    @Autowired
    private GenreService genreService;

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
        when(genreDAO.findByField("NAME", genre)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DataIntegrityViolationException.class, () -> genreService.newGenre(genre));
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