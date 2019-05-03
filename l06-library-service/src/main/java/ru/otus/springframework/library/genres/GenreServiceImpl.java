package ru.otus.springframework.library.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.dao.GenreDAO;
import ru.otus.springframework.library.genres.flux.GenreServiceFlux;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnMissingBean(GenreServiceFlux.class)
class GenreServiceImpl implements GenreService {

    private final GenreDAO genreDAO;

    @Override
    public List<Genre> all() {
        return genreDAO.findAll();
    }

    @Override
    public Genre newGenre(String name) {
        log.debug("new genre: {}", name);
        if (findByName(name).isPresent()) {
            throw new IllegalArgumentException("Genre " + name + " already exists!");
        }
        return genreDAO.saveObj(new Genre(name));
    }

    @Override
    public Optional<Genre> removeGenre(String name) {
        var genre = findByName(name);
        log.debug("found genre: {}", genre);
        try {
            return genre.flatMap((g -> genreDAO.deleteByObjId(g.getId())));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Can't delete genre [" + name + "]", e);
        }
    }

    private Optional<Genre> findByName(String name) {
        return genreDAO.findByName(name);
    }
}
