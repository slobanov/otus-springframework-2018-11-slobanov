package ru.otus.springframework.library.cli.presenters;

import org.springframework.shell.table.Table;

import java.util.List;

interface Presenter<T> {
    Table present(List<?> elems);
    Class<? super T> clz();
}
