package ru.otus.springframework.library.migration.job.readers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.PostConstruct;
import java.util.Map;

@RequiredArgsConstructor
class GenericReader<T> extends RepositoryItemReader<T> {
    private final PagingAndSortingRepository<T, Long> repository;

    @PostConstruct
    void init() {
        var sorts = Map.of("id", Sort.Direction.ASC);
        setRepository(repository);
        setSort(sorts);
        setMethodName("findAll");
    }
}
