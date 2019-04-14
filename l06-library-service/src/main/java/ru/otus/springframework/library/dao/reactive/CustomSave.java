package ru.otus.springframework.library.dao.reactive;

import reactor.core.publisher.Mono;

interface CustomSave<T> {
    Mono<T> saveObj(T obj);
}