package ru.otus.springframework.quiz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.auth.IOAuthService;
import ru.otus.springframework.quiz.i18n.I18nService;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.QuestionCSV;
import ru.otus.springframework.quiz.question.QuestionDAO;
import ru.otus.springframework.quiz.report.IOReportMessages;

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
@Slf4j
class QuizApplicationConfiguration {
    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    MessageSource messageSource(
            @Value("${quiz.i18n.auth}") String i18nAuth,
            @Value("${quiz.i18n.question}") String i18nQuestions
    ) {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(i18nAuth, i18nQuestions);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    QuestionDAO questionDAO(
            I18nService i18nService,
            @Value("${quiz.i18n.question.path}") String questionPath
    ) {
        return new QuestionCSV(i18nService.getMessage(questionPath));
    }

    @Bean
    AuthService authService(
            IOService ioService,
            I18nService i18nService,
            @Value("${quiz.i18n.auth.first-name}") String firstName,
            @Value("${quiz.i18n.auth.last-name}") String lastName
    ) {
        return new IOAuthService(
                ioService,
                i18nService.getMessage(firstName),
                i18nService.getMessage(lastName)
        );
    }

    @Bean
    IOReportMessages ioReportMessages(
            I18nService i18nService,
            @Value("${quiz.i18n.report}") String i18Report) {
        return new IOReportMessages(i18nService.getAllEntries(i18Report));
    }

}
