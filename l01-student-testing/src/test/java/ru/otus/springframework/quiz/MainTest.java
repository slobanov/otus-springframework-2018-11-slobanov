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
import org.springframework.context.MessageSource;
import ru.otus.springframework.quiz.io.IOService;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MainTest {

    private Main main;

    @BeforeEach
    void init() {
        main = new Main();
    }
    @ParameterizedTest
    @MethodSource("localeProvider")
    void locale(String localeTag, Locale locale) {
        assertThat(main.locale(localeTag), equalTo(locale));
    }

    private static Stream<Arguments> localeProvider() {
        return StreamEx.of(
                Arguments.of("ru-RU", Locale.forLanguageTag("ru-RU")),
                Arguments.of("en", Locale.ENGLISH),
                Arguments.of("random", Locale.getDefault())
        );
    }

    @ParameterizedTest
    @MethodSource("authParamProvider")
    void authService(String fName, String lName, Locale locale) {
        var messageSource = spy(MessageSource.class);
        var inOrder = inOrder(messageSource);

        main.authService(messageSource, mock(IOService.class), locale, fName, lName);

        inOrder.verify(messageSource).getMessage(eq(fName), any(), eq(locale));
        inOrder.verify(messageSource).getMessage(eq(lName), any(), eq(locale));
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
    @MethodSource("questionParamProvider")
    void questionDAO(String qPath, Locale locale) {
        var messageSource = spy(MessageSource.class);
        var inOrder = inOrder(messageSource);

        main.questionDAO(messageSource, locale, qPath);
        inOrder.verify(messageSource).getMessage(eq(qPath), any(), eq(locale));
        inOrder.verifyNoMoreInteractions();
    }

    private static Stream<Arguments> questionParamProvider() {
        return StreamEx.of(
                Arguments.of("some-path", Locale.FRANCE),
                Arguments.of("1/2/3", Locale.getDefault()),
                Arguments.of("qwerty", Locale.forLanguageTag("en-US"))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "i8n_tst/auth/auth_msg,i8n_tst/question/questions,en-US,test_auth_en_US,test_qst_en_US",
            "i8n_tst/auth/auth_msg,i8n_tst/question/questions,ru-RU,test_auth_ru_RU,test_qst_ru_RU",
    })
    void messageSource(
            String i8nAuth,
            String i8nQuestions,
            String tag,
            String authLine,
            String questionLine) {
        var messageSource = main.messageSource(i8nAuth, i8nQuestions);
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
    void ioReportMessages() throws NoSuchFieldException {
        var reportMessages = main.ioReportMessages(
                Locale.getDefault(), "i8n_tst/report/report_msg"
        );

        var textMap = Map.of(
                "headerText", "headerText1",
                "resultText", "resultText2",
                "actualText", "actualText3",
                "expectedText", "expectedText4",
                "correctText", "correctText5",
                "incorrectText", "incorrectText6"
        );

        EntryStream.of(textMap).mapKeys(
                key -> sneakyGetDeclaredStringField(reportMessages, key)
        ).forEach(pair -> {
            assertThat(pair.getKey(), equalTo(pair.getValue()));
        });
    }

    @SneakyThrows
    private static String sneakyGetDeclaredStringField(Object obj, String fieldName) {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (String) field.get(obj);
    }
}