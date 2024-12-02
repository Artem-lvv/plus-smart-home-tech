package ru.yandex.practicum.controller;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.payment_api.api.PaymentApi;
import ru.yandex.practicum.payment_api.model.OrderDto;
import ru.yandex.practicum.payment_api.model.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentController implements PaymentApi {
    @Override
    public ResponseEntity<BigDecimal> getTotalCost(OrderDto orderDto, OrderDto order) {
        return null;
    }

    @Override
    public ResponseEntity<PaymentDto> payment(OrderDto orderDto, OrderDto order) {
        return null;
    }

    @Override
    public ResponseEntity<Void> paymentFailed(UUID body, UUID order) {
        return null;
    }

    @Override
    public ResponseEntity<Void> paymentSuccess(UUID body, UUID order) {
        return null;
    }

    @Override
    public ResponseEntity<BigDecimal> productCost(OrderDto orderDto, OrderDto order) {
        return null;
    }
}
