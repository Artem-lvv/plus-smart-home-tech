package ru.yandex.practicum.converter;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.payment_api.model.OrderDto;

import java.util.stream.Collectors;

@Component
public class OrderToOrderDtoPayment implements Converter<Order, OrderDto> {
    @Override
    public OrderDto convert(Order order) {
        ru.yandex.practicum.payment_api.model.OrderDto orderDtoPayment =
                new ru.yandex.practicum.payment_api.model.OrderDto();
        orderDtoPayment.setOrderId(order.getOrderId());
        orderDtoPayment.setShoppingCartId(JsonNullable.of(order.getShoppingCartId()));
        orderDtoPayment.setProducts(order.getProducts()
                .stream()
                .collect(Collectors.toMap(orderProduct -> orderProduct.getOrderProductId().toString(),
                        OrderProduct::getQuantity)));
        orderDtoPayment.deliveryId(order.getDeliveryId());
        orderDtoPayment.totalPrice(order.getTotalPrice());
        orderDtoPayment.deliveryPrice(order.getDeliveryPrice());
        orderDtoPayment.productPrice(order.getProductPrice());

        return orderDtoPayment;
    }
}
