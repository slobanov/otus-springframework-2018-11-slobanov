package ru.otus.springframework.library.authors;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.authors.flux.AuthorServiceFlux;
import ru.otus.springframework.library.dao.AuthorDAO;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@ConditionalOnMissingBean(AuthorServiceFlux.class)
class AuthorServiceImpl implements AuthorService {

    private final AuthorDAO authorDAO;

    @Override
    public List<Author> all() {
        return authorDAO.findAll();
    }

    @Override
    public Optional<Author> withId(Long id) {
        return authorDAO.findById(id);
    }

    @Override
    public Author newAuthor(String firstName, String lastName) {
        try {
            return authorDAO.saveObj(new Author(firstName, lastName));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                    format("Can't save author [f = %s, l = %s]", firstName ,lastName),
                    e
            );
        }
    }

    @Override
    public Optional<Author> removeAuthor(Long id) {
        try {
            return authorDAO.deleteByObjId(id);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Can't delete author [" + id + "]", e);
        }
    }
}
