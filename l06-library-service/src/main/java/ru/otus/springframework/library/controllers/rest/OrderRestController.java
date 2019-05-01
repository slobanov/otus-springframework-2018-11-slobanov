package ru.otus.springframework.library.controllers.rest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.springframework.library.delivery.DeliveryTicket;
import ru.otus.springframework.library.order.Order;
import ru.otus.springframework.library.order.OrderGateway;

@Profile("rest")
@RestController
@RequiredArgsConstructor
@Slf4j
class OrderRestController {

    private final OrderGateway orderGateway;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v2/order")
    public DeliveryTicket makeOrder(@RequestBody Order order) {
        log.debug("order: {}", order);
        return orderGateway.placeOrder(order);
    }
}
