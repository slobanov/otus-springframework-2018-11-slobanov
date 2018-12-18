package ru.otus.springframework.quiz.question;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
class QuestionServiceImpl implements QuestionService {

    private final QuestionDAO questionDAO;

    QuestionServiceImpl(QuestionDAO questionDAO) {
        this.questionDAO = questionDAO;
    }

    @Override
    public List<Question> allQuestions() {
        return questionDAO.readAll();
    }
}
