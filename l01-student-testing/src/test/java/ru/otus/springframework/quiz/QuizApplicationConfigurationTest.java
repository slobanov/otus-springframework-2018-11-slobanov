package ru.otus.springframework.quiz;

import lombok.SneakyThrows;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ru.otus.springframework.quiz.i18n.I18nService;
import ru.otus.springframework.quiz.io.IOService;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

class QuizApplicationConfigurationTest {

    private QuizApplicationConfiguration quizApplicationConfiguration;

    @BeforeEach
    void init() {
        quizApplicationConfiguration = new QuizApplicationConfiguration();
    }

    @ParameterizedTest
    @MethodSource("authParamProvider")
    void authService(String fName, String lName, Locale locale) {
        var i18nService = spy(I18nService.class);
        var inOrder = inOrder(i18nService);

        quizApplicationConfiguration.authService(mock(IOService.class), i18nService, fName, lName);

        inOrder.verify(i18nService).getMessage(fName);
        inOrder.verify(i18nService).getMessage(lName);
        inOrder.verifyNoMoreInteractions();
    }

    private static Stream<Arguments> authParamProvider() {
        return StreamEx.of(
                Arguments.of("fname", "lname", Locale.FRANCE),
                Arguments.of("123", "qwe", Locale.getDefault()),
                Arguments.of("Hello", "World", Locale.forLanguageTag("en-US"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"some-path", "1/2/3", "qwerty"})
    void questionDAO(String qPath) {
        var i18nService = spy(I18nService.class);
        var inOrder = inOrder(i18nService);

        quizApplicationConfiguration.questionDAO(i18nService, qPath);
        inOrder.verify(i18nService).getMessage(qPath);
        inOrder.verifyNoMoreInteractions();
    }

    @ParameterizedTest
    @CsvSource({
            "i18n_tst/auth/auth_msg,i18n_tst/question/questions,en-US,test_auth_en_US,test_qst_en_US",
            "i18n_tst/auth/auth_msg,i18n_tst/question/questions,ru-RU,test_auth_ru_RU,test_qst_ru_RU",
    })
    void messageSource(
            String i18nAuth,
            String i18nQuestions,
            String tag,
            String authLine,
            String questionLine) {
        var messageSource = quizApplicationConfiguration.messageSource(i18nAuth, i18nQuestions);
        var locale = Locale.forLanguageTag(tag);

        assertThat(
                messageSource.getMessage("test_auth", null, locale),
                equalTo(authLine)
        );
        assertThat(
                messageSource.getMessage("test_qst", null, locale),
                equalTo(questionLine)
        );
    }

    @Test
    void ioReportMessages() {

        var i18Service = mock(I18nService.class);
        var textMap = Map.of(
                "headerText", "headerText1",
                "resultText", "resultText2",
                "actualText", "actualText3",
                "expectedText", "expectedText4",
                "correctText", "correctText5",
                "incorrectText", "incorrectText6"
        );

        var pathToReportMsg = "i18n_tst/report/report_msg";
        when(i18Service.getAllEntries(pathToReportMsg)).thenReturn(textMap);

        var reportMessages = quizApplicationConfiguration.ioReportMessages(
                i18Service, pathToReportMsg
        );

        EntryStream.of(textMap).mapKeys(
                key -> sneakyGetDeclaredStringField(reportMessages, key)
        ).forEach(pair -> assertThat(pair.getKey(), equalTo(pair.getValue())));
    }

    @SneakyThrows
    private static String sneakyGetDeclaredStringField(Object obj, String fieldName) {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(obj);
    }
}