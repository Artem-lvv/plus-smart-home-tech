package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.payment_api.api.PaymentApi;
import ru.yandex.practicum.payment_api.model.OrderDto;
import ru.yandex.practicum.payment_api.model.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {


    @Override
    public ResponseEntity<BigDecimal> getTotalCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public ResponseEntity<PaymentDto> payment(OrderDto orderDto) {
        return null;
    }

    @Override
    public ResponseEntity<Void> paymentFailed(UUID body) {
        return null;
    }

    @Override
    public ResponseEntity<Void> paymentSuccess(UUID body) {
        return null;
    }

    @Override
    public ResponseEntity<BigDecimal> productCost(OrderDto orderDto) {
        return null;
    }
}
