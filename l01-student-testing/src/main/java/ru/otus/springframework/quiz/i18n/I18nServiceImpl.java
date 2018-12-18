package ru.otus.springframework.quiz.i18n;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Service
@Slf4j
class I18nServiceImpl implements I18nService {

    private final Locale locale;
    private final MessageSource messageSource;

    I18nServiceImpl(@Value("${quiz.locale}") String localeTag, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = getLocale(localeTag);
    }

    static Locale getLocale(String localeTag) {
        var defaultLocale = Locale.getDefault();
        log.debug("Default locale = {}", defaultLocale);
        var configuredLocale = StreamEx.of(Locale.getAvailableLocales())
                .findAny(tag -> tag.toLanguageTag().equals(localeTag))
                .orElseGet(() -> {
                    log.warn(
                            "Failed to get locale for {}; using default {}",
                            localeTag,
                            defaultLocale.toLanguageTag()
                    );
                    return defaultLocale;
                });
        log.debug("Configured locale = {}", configuredLocale);
        return configuredLocale;
    }

    @Override
    public String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, locale);
    }

    @Override
    public Map<String, String> getAllEntries(String baseName) {
        var resourceBundle = ResourceBundle.getBundle(baseName, locale);
        return StreamEx.of(resourceBundle.getKeys())
                .mapToEntry(resourceBundle::getString)
                .toMap();
    }
}
