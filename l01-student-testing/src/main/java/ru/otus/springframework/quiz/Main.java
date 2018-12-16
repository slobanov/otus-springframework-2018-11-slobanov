package ru.otus.springframework.quiz;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.otus.springframework.quiz.auth.AuthService;
import ru.otus.springframework.quiz.auth.IOAuthService;
import ru.otus.springframework.quiz.io.IOService;
import ru.otus.springframework.quiz.question.QuestionCSV;
import ru.otus.springframework.quiz.question.QuestionDAO;
import ru.otus.springframework.quiz.report.IOReportMessages;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
@ComponentScan
@PropertySource("classpath:application.properties")
@Slf4j
public class Main {

    public static void main(String[] args) {
        try(
            var context = new AnnotationConfigApplicationContext(Main.class)
        ) {
            var quizApp = context.getBean(QuizApplication.class);
            quizApp.performQuiz();
        }
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean
    InputStream inputStream() {
        return System.in;
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    @Bean
    PrintStream printStream() {
        return System.out;
    }

    @Bean
    MessageSource messageSource(
            @Value("${quiz.i8n.auth}") String i8nAuth,
            @Value("${quiz.i8n.question}") String i8nQuestions
    ) {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(i8nAuth, i8nQuestions);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    QuestionDAO questionDAO(
            MessageSource messageSource,
            Locale locale,
            @Value("${quiz.i8n.question.path}") String questionPath
    ) {
        return new QuestionCSV(
                messageSource.getMessage(questionPath, null, locale)
        );
    }


    @Bean
    IOReportMessages ioReportMessages(
            Locale locale,
            @Value("${quiz.i8n.report}") String i8nReport
    ) {
        var resourceBundle = ResourceBundle.getBundle(i8nReport, locale);
        var reportMsgMap = StreamEx.of(resourceBundle.getKeys())
                .mapToEntry(resourceBundle::getString)
                .toMap();
        return new IOReportMessages(reportMsgMap);
    }

    @Bean
    Locale locale(@Value("${quiz.locale}") String locale) {
        var defaultLocale = Locale.getDefault();
        log.debug("Default locale = {}", defaultLocale);
        var configuredLocale = StreamEx.of(Locale.getAvailableLocales())
                .findAny(tag -> tag.toLanguageTag().equals(locale))
                .orElseGet(() -> {
                    log.warn(
                            "Failed to get locale for {}; using default {}",
                            locale,
                            defaultLocale.toLanguageTag()
                    );
                    return defaultLocale;
                });
        log.debug("Configured locale = {}", configuredLocale);
        return configuredLocale;
    }

    @Bean
    AuthService authService(
            MessageSource messageSource,
            IOService ioService,
            Locale locale,
            @Value("${quiz.i8n.auth.first-name}") String firstName,
            @Value("${quiz.i8n.auth.last-name}") String lastName
    ) {
        return new IOAuthService(
                ioService,
                messageSource.getMessage(firstName, null, locale),
                messageSource.getMessage(lastName, null, locale)
        );
    }

}

