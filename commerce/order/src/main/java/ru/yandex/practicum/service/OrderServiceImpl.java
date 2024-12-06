package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.order_api.model.CreateNewOrderRequest;
import ru.yandex.practicum.order_api.model.OrderDto;
import ru.yandex.practicum.order_api.model.ProductReturnRequest;
import ru.yandex.practicum.payment_api.api.PaymentApiClient;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;
import ru.yandex.practicum.warehouse_api.model.ShoppingCartDto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseApiClient warehouseApiClient;
    private final PaymentApiClient paymentApiClient;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

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
        UUID shoppingCartId = createNewOrderRequest.getShoppingCart().getShoppingCartId();

        ShoppingCartDto shoppingCartDtoWarehouseApi = new ShoppingCartDto(shoppingCartId,
                createNewOrderRequest.getShoppingCart().getProducts());

        warehouseApiClient.checkProductQuantityEnoughForShoppingCart(shoppingCartDtoWarehouseApi);

        Order order = Order.builder()
                .shoppingCartId(shoppingCartId)
                .state(OrderState.NEW)
                .build();

        List<OrderProduct> products = createNewOrderRequest.getShoppingCart().getProducts().entrySet()
                .stream()
                .map(entry -> OrderProduct.builder()
                        .orderProductId(UUID.fromString(entry.getKey()))
                        .order(order)
                        .quantity(entry.getValue())
                        .build())
                .toList();

        order.setProducts(products);

        orderRepository.save(order);
        log.info("Created new order: {}", order);

        return cs.convert(order, OrderDto.class);
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
        ru.yandex.practicum.payment_api.model.OrderDto orderDto = new ru.yandex.practicum.payment_api.model.OrderDto();
        orderDto.setOrderId(body);

        val payment = paymentApiClient.payment(orderDto);

        Order order = orderRepository.findById(body)
                .orElseThrow(() -> new EntityNotFoundException("Order", body.toString()));

        order.setPaymentId(Objects.requireNonNull(payment.getBody()).getPaymentId());

        orderRepository.save(order);
        log.info("Payment order: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto paymentFailed(UUID body, UUID orderId) {
        Order order = orderRepository.findById(body)
                .orElseThrow(() -> new EntityNotFoundException("Order", body.toString()));

        orderRepository.save(order);
        log.info("Payment failed order: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        return null;
    }
}
