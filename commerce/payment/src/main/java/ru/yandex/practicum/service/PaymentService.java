package ru.yandex.practicum.service;

import ru.yandex.practicum.payment_api.model.OrderDto;
import ru.yandex.practicum.payment_api.model.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    BigDecimal getTotalCost(OrderDto orderDto);

    PaymentDto payment(OrderDto orderDto);

    Void paymentFailed(UUID body);

    Void paymentSuccess(UUID body);

    BigDecimal productCost(OrderDto orderDto);

}
