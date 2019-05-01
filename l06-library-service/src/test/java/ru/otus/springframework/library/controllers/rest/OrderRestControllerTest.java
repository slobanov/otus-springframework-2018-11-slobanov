package ru.otus.springframework.library.controllers.rest;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.springframework.library.books.Book;
import ru.otus.springframework.library.delivery.DeliveryTicket;
import ru.otus.springframework.library.order.Order;
import ru.otus.springframework.library.order.OrderGateway;

import java.time.LocalDate;
import java.util.Set;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@WebMvcTest(OrderRestController.class)
@ActiveProfiles({"rest", "test-mongodb"})
class OrderRestControllerTest {

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private OrderGateway orderGateway;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        RestAssuredMockMvc.mockMvc(mvc);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_BOOK")
    void makeOrderTest() {
        var isbn = "123";
        var ticket = new DeliveryTicket(
                new Book(42L, isbn, "title", Set.of(), Set.of()),
                LocalDate.of(2010, 10, 10)
        );
        when(orderGateway.placeOrder(new Order(isbn))).thenReturn(ticket);

        var orderRequest = with()
                .contentType("application/json")
                .body(String.format("{ \"isbn\": %s}", isbn))
                .post("/api/v2/order");

        orderRequest.then().statusCode(201);
        assertThat(orderRequest.as(DeliveryTicket.class), equalTo(ticket));
    }

    @Test
    void makeOrderTestUnauthorized() {
        var isbn = "123";

        var orderRequest = with()
                .contentType("application/json")
                .body(String.format("{ \"isbn\": %s}", isbn))
                .post("/api/v2/order");

        orderRequest.then().statusCode(302);
    }

    @Test
    @WithMockUser(username = "test_user", password = "test_password", authorities = "ROLE_OTHER")
    void makeOrderTestForbidden() {
        var isbn = "123";

        var orderRequest = with()
                .contentType("application/json")
                .body(String.format("{ \"isbn\": %s}", isbn))
                .post("/api/v2/order");

        orderRequest.then().statusCode(403);
    }
}