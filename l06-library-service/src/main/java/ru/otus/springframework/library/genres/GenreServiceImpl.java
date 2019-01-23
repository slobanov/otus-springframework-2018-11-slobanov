package ru.otus.springframework.library.genres;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Service
@Slf4j
class GenreServiceImpl implements GenreService {

    private final SimpleDAO<Genre> genreDAO;

    GenreServiceImpl(SimpleDAO<Genre> genreDAO) {
        this.genreDAO = genreDAO;
    }

    @Override
    public List<Genre> all() {
        return genreDAO.fetchAll();
    }

    @Override
    public Genre newGenre(String name) {
        log.debug("new genre: {}", name);
        if (findByName(name).isPresent()) {
            throw new IllegalArgumentException("Genre " + name + " already exists!");
        }
        return genreDAO.save(new Genre(name));
    }

    @Override
    public Optional<Genre> removeGenre(String name) {
        var genre = findByName(name);
        log.debug("found genre: {}", genre);
        try {
            return genre.flatMap((g -> genreDAO.deleteById(g.getId())));
        } catch (DataIntegrityViolationException dive) {
            throw new IllegalArgumentException("Can't delete genre [" + name + "]", dive);
        }
    }

    private Optional<Genre> findByName(String name) {
        return asSingle(genreDAO.findByField("NAME", name));
    }
}
