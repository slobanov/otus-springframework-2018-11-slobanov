package ru.otus.springframework.library.genres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

import static ru.otus.springframework.library.utils.OptionalUtils.asSingle;

@Service
@Slf4j
@RequiredArgsConstructor
class GenreServiceImpl implements GenreService {

    private final SimpleDAO<Genre> genreDAO;

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
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Can't delete genre [" + name + "]", e);
        }
    }

    private Optional<Genre> findByName(String name) {
        return asSingle(genreDAO.findByField("NAME", name));
    }
}
