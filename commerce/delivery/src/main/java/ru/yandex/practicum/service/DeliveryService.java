package ru.yandex.practicum.service;

import ru.yandex.practicum.delivery_api.model.DeliveryDto;
import ru.yandex.practicum.delivery_api.model.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    BigDecimal deliveryCost(OrderDto orderDto);

    Void deliveryFailed(UUID body);

    Void deliveryPicked(UUID body);

    Void deliverySuccessful(UUID body);

    DeliveryDto planDelivery(DeliveryDto deliveryDto);
}
