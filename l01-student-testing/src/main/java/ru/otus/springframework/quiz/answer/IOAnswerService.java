package ru.otus.springframework.quiz.answer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.Question;

@Service
@Slf4j
class IOAnswerService implements AnswerService {

    private final IOService ioService;

    IOAnswerService(IOService ioService) {
        this.ioService = ioService;
    }

    @Override
    public Answer reply(Question question) {
        log.debug("question = {}", question);
        var answerText = ioService.ask(question.getText());
        log.debug("answerText = {}", answerText);
        return new Answer(question, answerText);
    }
}
