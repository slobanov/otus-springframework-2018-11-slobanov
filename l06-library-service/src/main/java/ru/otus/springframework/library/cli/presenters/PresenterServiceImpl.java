package ru.otus.springframework.library.cli.presenters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.shell.table.Table;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
class PresenterServiceImpl implements PresenterService {

    private final List<Presenter<?>> presenters;

    @Override
    public <T> Table present(List<T> elems, Class<? super T> clz) {
        log.debug("Presenting: {}; as class: {}", elems, clz);
        var presenter = StreamEx.of(presenters)
                .findAny(p -> clz.equals(p.clz()))
                .orElseThrow(
                        () -> new IllegalArgumentException("There is no suitable presenter for " + clz)
                );
        return presenter.present(elems);
    }

}
