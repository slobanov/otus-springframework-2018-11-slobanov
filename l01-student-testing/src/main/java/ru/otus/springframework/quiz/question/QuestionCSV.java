package ru.otus.springframework.quiz.question;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.dataformat.csv.CsvSchema.emptySchema;

@Slf4j
class QuestionCSV implements QuestionDAO {

    private final String filePath;

    QuestionCSV(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Question> readAll() {
        var mapper = new CsvMapper();
        try(
            var file = new ClassPathResource(filePath).getInputStream();
            MappingIterator<Question> questionItr = mapper
                    .readerFor(Question.class)
                    .with(emptySchema().withHeader())
                    .readValues(file)
        ) {
            return questionItr.readAll();
        } catch (IOException e) {
            log.error("Failed to read questions, returning empty list instead", e);
            return Collections.emptyList();
        }
    }
}
