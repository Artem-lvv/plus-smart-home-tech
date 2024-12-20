package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.order_api.api.OrderApi;
import ru.yandex.practicum.order_api.model.CreateNewOrderRequest;
import ru.yandex.practicum.order_api.model.OrderDto;
import ru.yandex.practicum.order_api.model.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderApi {
    private final OrderService orderService;

    @Override
    public ResponseEntity<OrderDto> assembly(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.assembly(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> assemblyFailed(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.assemblyFailed(body, orderId), HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<OrderDto> calculateDeliveryCost(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.calculateDeliveryCost(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> calculateTotalCost(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.calculateTotalCost(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> complete(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.complete(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> createNewOrder(CreateNewOrderRequest createNewOrderRequest) {
        return new ResponseEntity<>(orderService.createNewOrder(createNewOrderRequest), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> delivery(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.delivery(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> deliveryFailed(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.deliveryFailed(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<OrderDto>> getClientOrders(String username) {
        return new ResponseEntity<>(orderService.getClientOrders(username), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> payment(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.payment(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> paymentFailed(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.paymentFailed(body, orderId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> paymentSuccess(UUID body, UUID orderId) {
        return new ResponseEntity<>(orderService.paymentSuccess(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OrderDto> productReturn(ProductReturnRequest productReturnRequest) {
        return new ResponseEntity<>(orderService.productReturn(productReturnRequest), HttpStatus.OK);
    }
}
