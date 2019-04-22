package ru.otus.springframework.library.migration.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.genres.Genre;
import ru.otus.springframework.library.migration.job.writers.CommentWriterTemplate;

@Configuration
@EnableBatchProcessing
class MigrationConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoOperations mongoOperations;

    @Bean
    <T> ItemWriter<T> writer() {
        return new MongoItemWriterBuilder<T>()
                .template(mongoOperations)
                .build();
    }

    @Bean
    ItemWriter<Comment> commentWriter() {
        var commentWriter = new CommentWriterTemplate();
        commentWriter.setTemplate(mongoOperations);
        return commentWriter;
    }

    @Bean
    public Job migrationJob(
            JobBuilderFactory jobBuilderFactory,
            @Qualifier("migrateGenres") Step migrateGenres,
            @Qualifier("migrateAuthors") Step migrateAuthors,
            @Qualifier("migrateBooks") Step migrateBooks,
            @Qualifier("migrateComments") Step migrateComments
    ) {
        return jobBuilderFactory.get("migrationJob")
                .incrementer(new RunIdIncrementer())
                .flow(migrateGenres)
                .next(migrateAuthors)
                .next(migrateBooks)
                .next(migrateComments)
                .end()
                .build();
    }

    @Bean
    public Step migrateGenres(
            ItemReader<Genre> reader,
            ItemWriter<Genre> writer,
            @Value("${library.migration.chunk.size}") int chunkSize
    ) {
        return genericStep("migrateGenres", reader, writer, chunkSize);
    }

    @Bean
    public Step migrateAuthors(
            ItemReader<Author> reader,
            ItemWriter<Author> writer,
            @Value("${library.migration.chunk.size}") int chunkSize
    ) {
        return genericStep("migrateAuthors", reader, writer, chunkSize);
    }

    @Bean
    public Step migrateBooks(
            ItemReader<Book> reader,
            ItemWriter<Book> writer,
            @Value("${library.migration.chunk.size}") int chunkSize
    ) {
        return genericStep("migrateBooks", reader, writer, chunkSize);
    }

    @Bean
    public Step migrateComments(
            ItemReader<Comment> reader,
            ItemWriter<Comment> writer,
            @Value("${library.migration.chunk.size}") int chunkSize

    ) {
        return genericStep("migrateComments", reader, writer, chunkSize);
    }

    private <T> Step genericStep(
            String name,
            ItemReader<T> reader,
            ItemWriter<T> writer,
            int chunkSize
    ) {
        return stepBuilderFactory.get(name)
                .<T, T>chunk(chunkSize)
                .reader(reader)
                .writer(writer)
                .build();
    }

}
