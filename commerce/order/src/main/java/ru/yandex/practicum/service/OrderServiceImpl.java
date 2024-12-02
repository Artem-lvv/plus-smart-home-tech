package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order_api.model.CreateNewOrderRequest;
import ru.yandex.practicum.order_api.model.OrderDto;
import ru.yandex.practicum.order_api.model.ProductReturnRequest;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderDto assembly(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyFailed(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalCost(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto complete(UUID body, UUID orderId) {
        return null;
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createNewOrderRequest) {

        return null;
    }

    @Override
    public OrderDto delivery(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public List<OrderDto> getClientOrders(String username) {
        return List.of();
    }

    @Override
    public OrderDto payment(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto paymentFailed(UUID body, UUID orderId) {
        return null;
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        return null;
    }
}
