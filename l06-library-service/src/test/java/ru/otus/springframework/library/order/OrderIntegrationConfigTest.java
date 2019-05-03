package ru.otus.springframework.library.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.springframework.library.authors.AuthorService;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.books.BookService;
import ru.otus.springframework.library.comments.Comment;
import ru.otus.springframework.library.comments.CommentService;
import ru.otus.springframework.library.dao.BookDAO;
import ru.otus.springframework.library.dao.CommentDAO;
import ru.otus.springframework.library.delivery.DeliveryService;
import ru.otus.springframework.library.delivery.DeliveryTicket;
import ru.otus.springframework.library.genres.GenreService;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderIntegrationConfigTest {

    @Autowired
    private OrderGateway orderGateway;

    @SpyBean
    private CommentService commentService;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private DeliveryService deliveryService;

    @MockBean
    private BookDAO bookDAO;

    @MockBean
    private CommentDAO commentDAO;

    @Test
    void happyPath() {
        var isbn = "123";
        var order = new Order(isbn);
        var book = mock(Book.class);
        var commentText = "This book was bought just now!";
        var now = LocalDate.now();
        var ticket = new DeliveryTicket(book, now);

        when(book.getIsbn()).thenReturn(isbn);
        when(bookService.withIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookDAO.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(commentDAO.saveObj(any(Comment.class))).thenReturn(mock(Comment.class));
        when(deliveryService.bookDelivery(book)).thenReturn(ticket);

        var resultTicket = orderGateway.placeOrder(order);

        assertThat(resultTicket, equalTo(ticket));

        verify(bookService).withIsbn(isbn);
        verify(deliveryService).bookDelivery(book);
        verify(commentService).newComment(isbn, commentText);
    }

    @Test
    void noBook() {
        var isbn = "123";
        var order = new Order(isbn);

        when(bookService.withIsbn(isbn)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderGateway.placeOrder(order));

        verify(bookService).withIsbn(isbn);
        verify(commentService, never()).newComment(anyString(), anyString());
    }

}