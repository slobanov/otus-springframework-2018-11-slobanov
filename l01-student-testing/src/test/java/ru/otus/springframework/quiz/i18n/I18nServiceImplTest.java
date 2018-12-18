package ru.otus.springframework.quiz.i18n;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class I18nServiceImplTest {

    @ParameterizedTest
    @MethodSource("localeAndMsgProvider")
    void getMessage(String msg, Locale locale) {
        var messageSource = mock(MessageSource.class);
        var i18Service = new I18nServiceImpl(locale.toLanguageTag(), messageSource);

        when(messageSource.getMessage(msg, null, locale)).thenReturn(msg);
        assertThat(i18Service.getMessage(msg), equalTo(msg));
    }

    @Test
    void getAllEntries() {
        var textMap = Map.of(
                "headerText", "headerText1",
                "resultText", "resultText2",
                "actualText", "actualText3",
                "expectedText", "expectedText4",
                "correctText", "correctText5",
                "incorrectText", "incorrectText6"
        );
        var i18Service = new I18nServiceImpl(
                Locale.getDefault().toLanguageTag(), mock(MessageSource.class)
        );
        assertThat(
                i18Service.getAllEntries("i18n_tst/report/report_msg"),
                equalTo(textMap)
        );

    }

    private static Stream<Arguments> localeAndMsgProvider() {
        return StreamEx.of(
                Arguments.of("msg1", Locale.FRANCE),
                Arguments.of("qwe", Locale.getDefault()),
                Arguments.of("Hello, World", Locale.forLanguageTag("en-US"))
        );
    }

    @ParameterizedTest
    @MethodSource("localeProvider")
    void getLocale(String localeTag, Locale locale) {
        assertThat(I18nServiceImpl.getLocale(localeTag), equalTo(locale));
    }

    private static Stream<Arguments> localeProvider() {
        return StreamEx.of(
                Arguments.of("ru-RU", Locale.forLanguageTag("ru-RU")),
                Arguments.of("en", Locale.ENGLISH),
                Arguments.of("random", Locale.getDefault())
        );
    }

}