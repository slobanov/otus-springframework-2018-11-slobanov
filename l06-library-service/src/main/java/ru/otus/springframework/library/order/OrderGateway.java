package ru.otus.springframework.library.order;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.springframework.library.delivery.DeliveryTicket;

import static ru.otus.springframework.library.order.OrderIntegrationConfig.DELIVERY_TICKETS_CHANNEL;
import static ru.otus.springframework.library.order.OrderIntegrationConfig.PLACED_ORDERS_CHANNEL;

@MessagingGateway
public interface OrderGateway {
    @Gateway(requestChannel = PLACED_ORDERS_CHANNEL, replyChannel = DELIVERY_TICKETS_CHANNEL)
    DeliveryTicket placeOrder(Order order);
}
