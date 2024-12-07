package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.payment_api.api.PaymentApi;
import ru.yandex.practicum.payment_api.model.OrderDto;
import ru.yandex.practicum.payment_api.model.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<BigDecimal> getTotalCost(OrderDto orderDto) {
        return new ResponseEntity<>(paymentService.getTotalCost(orderDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaymentDto> payment(OrderDto orderDto) {
        return new ResponseEntity<>(paymentService.payment(orderDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> paymentFailed(UUID body) {
        return new ResponseEntity<>(paymentService.paymentFailed(body), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Void> paymentSuccess(UUID body) {
        return new ResponseEntity<>(paymentService.paymentSuccess(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BigDecimal> productCost(OrderDto orderDto) {
        return new ResponseEntity<>(paymentService.productCost(orderDto), HttpStatus.OK);
    }
}
