package ru.otus.springframework.library.cli.presenters;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
class PresenterImpl<T> implements Presenter<T> {

    private final Class<? super T> clz;
    private final Supplier<EntryStream<String, Object>> headerSupplier;

    PresenterImpl(Class<? super T> clz, Supplier<EntryStream<String, Object>> headerSupplier) {
        this.clz = clz;
        this.headerSupplier = headerSupplier;
    }

    @Override
    public Table present(List<?> elems) {
        log.debug("Presenting: {}", elems);

        var header = fillHeader();
        log.debug("Got header: {}", header);

        var model = new BeanListTableModel<>(
                elems,
                header
        );
        var tableBuilder = new TableBuilder(model);
        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }

    private LinkedHashMap<String, Object> fillHeader() {
        var header = new LinkedHashMap<String, Object>();
        headerSupplier.get().forEach(
                pair -> header.put(pair.getKey(), pair.getValue())
        );
        return header;
    }

    @Override
    public Class<? super T> clz() {
        return clz;
    }

}
