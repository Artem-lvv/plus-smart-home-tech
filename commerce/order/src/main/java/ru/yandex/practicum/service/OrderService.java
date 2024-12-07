package ru.yandex.practicum.service;

import ru.yandex.practicum.order_api.model.CreateNewOrderRequest;
import ru.yandex.practicum.order_api.model.OrderDto;
import ru.yandex.practicum.order_api.model.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderDto assembly(UUID body, UUID orderId);

    OrderDto assemblyFailed(UUID body, UUID orderId);

    OrderDto calculateDeliveryCost(UUID body, UUID orderId);

    OrderDto calculateTotalCost(UUID body, UUID orderId);

    OrderDto complete(UUID body, UUID orderId);

    OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest);

    OrderDto delivery(UUID body, UUID orderId);

    OrderDto deliveryFailed(UUID body, UUID orderId);

    List<OrderDto> getClientOrders(String username);

    OrderDto payment(UUID body, UUID orderId);

    OrderDto paymentFailed(UUID body, UUID orderId);

    OrderDto productReturn(ProductReturnRequest productReturnRequest);

    OrderDto paymentSuccess(UUID body);
}
