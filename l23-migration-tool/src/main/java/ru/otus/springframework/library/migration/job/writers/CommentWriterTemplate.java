package ru.otus.springframework.library.migration.job.writers;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.dao.mongodb.BookComments;
import ru.otus.springframework.library.dao.mongodb.MongoComment;

import java.util.List;

import static org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public class CommentWriterTemplate extends MongoItemWriter<Comment> {

    @Override
    protected void doWrite(List<? extends Comment> items) {
        var ops = getTemplate().bulkOps(UNORDERED, BookComments.class);
        items.forEach(comment -> ops.updateOne(
                query(where("_id").is(comment.getBook().getId())),
                new Update().push("comments", MongoComment.fromComment(comment))
        ));
        ops.execute();
    }
}
