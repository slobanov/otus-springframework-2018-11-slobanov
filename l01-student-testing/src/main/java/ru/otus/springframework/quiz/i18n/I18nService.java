package ru.otus.springframework.quiz.i18n;

import java.util.Map;

public interface I18nService {
    String getMessage(String messageKey);
    Map<String, String> getAllEntries(String baseName);
}
