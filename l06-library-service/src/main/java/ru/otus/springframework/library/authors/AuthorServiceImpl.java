package ru.otus.springframework.library.authors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.dao.SimpleDAO;

import java.util.List;
import java.util.Optional;

@Service
class AuthorServiceImpl implements AuthorService {

    private final SimpleDAO<Author> authorDAO;

    AuthorServiceImpl(SimpleDAO<Author> authorDAO) {
        this.authorDAO = authorDAO;
    }

    @Override
    public List<Author> all() {
        return authorDAO.fetchAll();
    }

    @Override
    public Optional<Author> withId(Long id) {
        return authorDAO.findById(id);
    }

    @Override
    public Author newAuthor(String firstName, String lastName) {
        return authorDAO.save(new Author(firstName, lastName));
    }

    @Override
    public Optional<Author> removeAuthor(Long id) {
        try {
            return authorDAO.deleteById(id);
        } catch (DataIntegrityViolationException dive) {
            throw new IllegalArgumentException("Can't delete author [" + id + "]", dive);
        }
    }
}
