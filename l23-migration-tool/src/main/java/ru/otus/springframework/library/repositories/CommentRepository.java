package ru.otus.springframework.library.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.otus.springframework.library.comments.Comment;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {}
