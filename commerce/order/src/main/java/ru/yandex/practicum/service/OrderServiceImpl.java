package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery_api.api.DeliveryApiClient;
import ru.yandex.practicum.delivery_api.model.AddressDto;
import ru.yandex.practicum.delivery_api.model.DeliveryDto;
import ru.yandex.practicum.delivery_api.model.DeliveryState;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.model.OrderProduct;
import ru.yandex.practicum.model.OrderState;
import ru.yandex.practicum.order_api.model.CreateNewOrderRequest;
import ru.yandex.practicum.order_api.model.OrderDto;
import ru.yandex.practicum.order_api.model.ProductReturnRequest;
import ru.yandex.practicum.payment_api.api.PaymentApiClient;
import ru.yandex.practicum.payment_api.model.PaymentDto;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;
import ru.yandex.practicum.warehouse_api.model.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse_api.model.BookedProductsDto;
import ru.yandex.practicum.warehouse_api.model.ShoppingCartDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WarehouseApiClient warehouseApiClient;
    private final PaymentApiClient paymentApiClient;
    private final DeliveryApiClient deliveryApiClient;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    public OrderDto assembly(UUID body, UUID orderId) {
        Order order = getOrder(body);

        AssemblyProductForOrderFromShoppingCartRequest request = new AssemblyProductForOrderFromShoppingCartRequest();
        request.setOrderId(order.getOrderId());
        request.setShoppingCartId(order.getShoppingCartId());

        BookedProductsDto bookedProductsDto = warehouseApiClient
                .assemblyProductForOrderFromShoppingCart(request).getBody();

        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setState(OrderState.ASSEMBLED);

        orderRepository.save(order);
        log.info("Order assembly completed: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto assemblyFailed(UUID body, UUID orderId) {
        Order order = getOrder(body);
        order.setState(OrderState.ASSEMBLY_FAILED);

        orderRepository.save(order);
        log.info("Order assembly failed: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID body, UUID orderId) {
        Order order = getOrder(body);

        ru.yandex.practicum.delivery_api.model.OrderDto orderDtoDelivery = getOrderDtoDelivery(order);

        BigDecimal deliveryCost = deliveryApiClient.deliveryCost(orderDtoDelivery).getBody();
        order.setDeliveryPrice(deliveryCost);
        orderRepository.save(order);
        log.info("Order calculated delivery cost: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    private static ru.yandex.practicum.delivery_api.model.OrderDto getOrderDtoDelivery(Order order) {
        Map<String, Long> productToQuantity = order.getProducts()
                .stream()
                .collect(Collectors.toMap(orderProduct -> orderProduct.getOrderProductId().toString(),
                        OrderProduct::getQuantity));

        ru.yandex.practicum.delivery_api.model.OrderDto orderDtoDelivery =
                new ru.yandex.practicum.delivery_api.model.OrderDto();

        orderDtoDelivery.setOrderId(order.getOrderId());
        orderDtoDelivery.setShoppingCartId(JsonNullable.of(order.getShoppingCartId()));
        orderDtoDelivery.setProducts(productToQuantity);
        orderDtoDelivery.setPaymentId(order.getPaymentId());
        orderDtoDelivery.setDeliveryId(order.getDeliveryId());
        orderDtoDelivery.setState(ru.yandex.practicum.delivery_api.model.OrderDto.StateEnum.fromValue(order.getState()
                .toString()));
        orderDtoDelivery.setDeliveryWeight(order.getDeliveryWeight());
        orderDtoDelivery.setDeliveryVolume(order.getDeliveryVolume());
        orderDtoDelivery.setFragile(order.getFragile());
        orderDtoDelivery.setTotalPrice(order.getTotalPrice());
        orderDtoDelivery.setDeliveryPrice(order.getDeliveryPrice());
        orderDtoDelivery.setProductPrice(order.getProductPrice());
        return orderDtoDelivery;
    }

    @Override
    public OrderDto calculateTotalCost(UUID body, UUID orderId) {
        Order order = getOrder(body);

        ru.yandex.practicum.payment_api.model.OrderDto orderDtoPayment = cs
                .convert(order, ru.yandex.practicum.payment_api.model.OrderDto.class);

        BigDecimal totalCost = paymentApiClient.getTotalCost(orderDtoPayment).getBody();
        order.setTotalPrice(totalCost);
        orderRepository.save(order);
        log.info("Order calculated cost: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto complete(UUID body, UUID orderId) {
        Order order = getOrder(body);
        order.setState(OrderState.COMPLETED);

        orderRepository.save(order);
        log.info("Order completed: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
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

        DeliveryDto deliveryDtoResponse = getDeliveryDto(createNewOrderRequest, order);
        order.setDeliveryId(deliveryDtoResponse.getDeliveryId());

        orderRepository.save(order);
        log.info("Created new order: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    private DeliveryDto getDeliveryDto(CreateNewOrderRequest createNewOrderRequest, Order order) {

        AddressDto toAddressDtoDelivery = new AddressDto();
        toAddressDtoDelivery.setCountry(createNewOrderRequest.getDeliveryAddress().getCountry());
        toAddressDtoDelivery.setCity(createNewOrderRequest.getDeliveryAddress().getCity());
        toAddressDtoDelivery.setStreet(createNewOrderRequest.getDeliveryAddress().getStreet());
        toAddressDtoDelivery.setHouse(createNewOrderRequest.getDeliveryAddress().getHouse());
        toAddressDtoDelivery.setFlat(createNewOrderRequest.getDeliveryAddress().getFlat());

        ru.yandex.practicum.warehouse_api.model.AddressDto fromAddress = warehouseApiClient.getWarehouseAddress()
                .getBody();

        AddressDto fromAddressDtoDe = new AddressDto();
        fromAddressDtoDe.setCountry(fromAddress.getCountry());
        fromAddressDtoDe.setCity(fromAddress.getCity());
        fromAddressDtoDe.setStreet(fromAddress.getStreet());
        fromAddressDtoDe.setHouse(fromAddress.getHouse());
        fromAddressDtoDe.setFlat(fromAddress.getFlat());

        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setDeliveryState(DeliveryState.CREATED);
        deliveryDto.setToAddress(toAddressDtoDelivery);
        deliveryDto.setFromAddress(fromAddressDtoDe);
        deliveryDto.setOrderId(order.getOrderId());

        DeliveryDto deliveryDtoResponse = deliveryApiClient.planDelivery(deliveryDto).getBody();

        return deliveryDtoResponse;
    }

    @Override
    public OrderDto delivery(UUID body, UUID orderId) {
        Order order = getOrder(body);
        order.setState(OrderState.DELIVERED);

        orderRepository.save(order);
        log.info("Order delivered: {}", order);

        return null;
    }

    @Override
    public OrderDto deliveryFailed(UUID body, UUID orderId) {
        Order order = getOrder(body);
        order.setState(OrderState.DELIVERY_FAILED);

        orderRepository.save(order);
        log.info("Order delivery failed: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getClientOrders(String username) {
        List<Order> allByUsername = orderRepository.findAllByUsername(username);

        return allByUsername
                .stream()
                .map(order -> cs.convert(order, OrderDto.class))
                .toList();
    }

    @Override
    public OrderDto payment(UUID body, UUID orderId) {
        Order order = getOrder(body);

        ru.yandex.practicum.payment_api.model.OrderDto orderDtoPayment = cs
                .convert(order, ru.yandex.practicum.payment_api.model.OrderDto.class);

        PaymentDto payment = paymentApiClient.payment(orderDtoPayment).getBody();

        order.setPaymentId(Objects.nonNull(payment.getPaymentId()) ? payment.getPaymentId() : null);
        order.setState(OrderState.ON_PAYMENT);

        orderRepository.save(order);
        log.info("Payment ON_PAYMENT order: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto paymentFailed(UUID body, UUID orderId) {
        Order order = getOrder(body);
        order.setState(OrderState.PAYMENT_FAILED);

        orderRepository.save(order);
        log.info("Payment PAYMENT_FAILED order: {}", order);

        return cs.convert(order, OrderDto.class);
    }

    private Order getOrder(UUID body) {
        return orderRepository.findById(body)
                .orElseThrow(() -> new EntityNotFoundException("Order", body.toString()));
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest productReturnRequest) {
        Order order = getOrder(productReturnRequest.getOrderId());
        order.setState(OrderState.PRODUCT_RETURNED);

        orderRepository.save(order);
        log.info("Product return order: {}", order);

        warehouseApiClient.acceptReturn(productReturnRequest.getProducts());

        return cs.convert(order, OrderDto.class);
    }

    @Override
    public OrderDto paymentSuccess(UUID body) {
        Order order = getOrder(body);
        order.setState(OrderState.PAID);

        orderRepository.save(order);
        log.info("Payment PAID order: {}", order);

        return cs.convert(order, OrderDto.class);
    }
}
