package ru.otus.springframework.quiz.question;

import java.util.List;

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
