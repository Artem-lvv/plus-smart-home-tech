package ru.yandex.practicum.converter;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.order_api.model.OrderDto;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderToOrderDtoConverter implements Converter<Order, OrderDto> {
    @Override
    public OrderDto convert(Order src) {
        OrderDto dto = new OrderDto();
        dto.setOrderId(src.getOrderId());
        dto.setShoppingCartId(JsonNullable.of(src.getShoppingCartId()));
        dto.setProducts(Objects.isNull(src.getProducts()) ? null : src.getProducts()
                .stream()
                .collect(Collectors.toMap(orderProduct -> orderProduct.getOrderProductId().toString(),
                        OrderProduct::getQuantity)));
        dto.paymentId(Objects.isNull(src.getPaymentId()) ? null : src.getPaymentId());
        dto.deliveryId(Objects.isNull(src.getDeliveryId()) ? null : src.getDeliveryId());
        dto.setState(Objects.isNull(src.getState()) ? null : OrderDto.StateEnum.fromValue(src.getState().toString()));
        dto.deliveryWeight(src.getDeliveryWeight());
        dto.deliveryVolume(src.getDeliveryVolume());
        dto.fragile(src.getFragile());
        dto.deliveryPrice(src.getDeliveryPrice());
        dto.productPrice(src.getProductPrice());

        return dto;
    }
}
