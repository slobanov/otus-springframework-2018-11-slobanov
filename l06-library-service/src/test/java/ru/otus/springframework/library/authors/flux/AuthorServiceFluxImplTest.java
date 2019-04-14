package ru.otus.springframework.library.authors.flux;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.springframework.library.authors.Author;
import ru.otus.springframework.library.dao.reactive.ReactiveAuthorMongodbRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceFluxImplTest {

    @Mock
    private ReactiveAuthorMongodbRepository authorRepository;

    @InjectMocks
    private AuthorServiceFluxImpl authorService;

    @Test
    void all() {
        when(authorRepository.findAll()).thenReturn(Flux.empty());
        StepVerifier.create(authorService.all())
                .expectComplete();

        verify(authorRepository).findAll();
    }

    @Test
    void withId() {
        var authorId = 1L;
        var expectedAuthor = new Author(1L, "fName1", "lName1");
        when(authorRepository.findById(authorId)).thenReturn(Mono.just(expectedAuthor));

        StepVerifier.create(authorService.withId(authorId))
                .assertNext(author -> assertThat(author, equalTo(expectedAuthor)))
                .verifyComplete();

        verify(authorRepository).findById(authorId);

    }

    @Test
    void withIdEmpty() {
        var authorId = 42L;
        when(authorRepository.findById(authorId)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.withId(authorId))
                .verifyComplete();

        verify(authorRepository).findById(authorId);
    }


    @ParameterizedTest
    @CsvSource({
            "fName1,lName1",
            "fName2,lName2"
    })
    void newAuthor(String fName, String lName) {
        var authorCaptor = ArgumentCaptor.forClass(Author.class);
        when(authorRepository.saveObj(any(Author.class)))
                .thenReturn(Mono.just(new Author(fName, lName)));

        StepVerifier.create(authorService.newAuthor(fName, lName))
                .assertNext(a -> {
                    assertThat(a.getFirstName(), equalTo(fName));
                    assertThat(a.getLastName(), equalTo(lName));
                })
                .verifyComplete();

        verify(authorRepository).saveObj(authorCaptor.capture());
        var author = authorCaptor.getValue();

        assertThat(fName, equalTo(author.getFirstName()));
        assertThat(lName, equalTo(author.getLastName()));
    }

    @Test
    void removeAuthor() {
        var id = 1L;
        var expectedAuthor = mock(Author.class);
        when(authorRepository.deleteByObjId(id)).thenReturn(Mono.just(expectedAuthor));

        StepVerifier.create(authorService.removeAuthor(id))
                .assertNext(author -> assertThat(author, equalTo(expectedAuthor)))
                .verifyComplete();

        verify(authorRepository).deleteByObjId(id);
    }

    @Test
    void removeAuthorEmpty() {
        var id = 42L;
        when(authorRepository.deleteByObjId(id)).thenReturn(Mono.empty());

        StepVerifier.create(authorService.removeAuthor(id)).expectComplete();
        verify(authorRepository).deleteByObjId(id);
    }
}