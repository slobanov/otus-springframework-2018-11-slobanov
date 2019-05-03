package ru.otus.springframework.library.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import ru.otus.springframework.library.books.Book;

import java.util.Map;
import java.util.Optional;

import static org.springframework.integration.dsl.MessageChannels.publishSubscribe;

@EnableIntegration
@Configuration
@IntegrationComponentScan
class OrderIntegrationConfig {

    static final String PLACED_ORDERS_CHANNEL = "placedOrdersChannel";
    static final String DELIVERY_TICKETS_CHANNEL = "deliveryTicketsChannel";

    private static final String BOOKS_TO_DELIVER_CHANNEL = "booksToDeliverChannel";
    private static final String ERROR_CHANNEL = "errorChannel";

    @Bean
    public IntegrationFlow placeOrderFlow() {
        return IntegrationFlows.from(PLACED_ORDERS_CHANNEL)
                .transform(Order::getIsbn)
                .handle("bookService", "withIsbn")
                .<Optional<Book>, Boolean>route(
                        Optional::isPresent,
                        mapping -> mapping
                                .subFlowMapping(
                                        true,
                                        sf -> sf
                                                .<Optional<Book>, Book>transform(Optional::get)
                                                .channel(publishSubscribe(BOOKS_TO_DELIVER_CHANNEL))
                                )
                                .subFlowMapping(
                                        false,
                                        sf -> sf.transform(o -> "There is no such book")
                                                .channel(ERROR_CHANNEL)
                                )
                ).get();
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return IntegrationFlows.from(ERROR_CHANNEL)
                .transform(OrderException::new)
                .channel(DELIVERY_TICKETS_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow deliveryFlow() {
        return IntegrationFlows.from(BOOKS_TO_DELIVER_CHANNEL)
                .handle("deliveryService", "bookDelivery")
                .channel(DELIVERY_TICKETS_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow addCommentFlow() {
        return IntegrationFlows.from(BOOKS_TO_DELIVER_CHANNEL)
                .transform(Book::getIsbn)
                .enrichHeaders(Map.of("text", "This book was bought just now!"))
                .handle("commentService", "newComment")
                .nullChannel();
    }
}
