package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.delivery_api.api.DeliveryApi;
import ru.yandex.practicum.delivery_api.model.DeliveryDto;
import ru.yandex.practicum.delivery_api.model.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryController implements DeliveryApi {
    private final DeliveryService deliveryService;

    @Override
    public ResponseEntity<BigDecimal> deliveryCost(OrderDto orderDto) {
        return new ResponseEntity<>(deliveryService.deliveryCost(orderDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deliveryFailed(UUID body) {
        return new ResponseEntity<>(deliveryService.deliveryFailed(body), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Void> deliveryPicked(UUID body) {
        return new ResponseEntity<>(deliveryService.deliveryPicked(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deliverySuccessful(UUID body) {
        return new ResponseEntity<>(deliveryService.deliverySuccessful(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DeliveryDto> planDelivery(DeliveryDto deliveryDto) {
        return new ResponseEntity<>(deliveryService.planDelivery(deliveryDto), HttpStatus.OK);
    }
}
