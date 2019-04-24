package ru.otus.springframework.library.migration.job.readers;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ru.otus.springframework.library.comments.Comment;

@Service
class CommentReader extends GenericReader<Comment> {
    CommentReader(PagingAndSortingRepository<Comment, Long> repository) {
        super(repository);
    }
}
