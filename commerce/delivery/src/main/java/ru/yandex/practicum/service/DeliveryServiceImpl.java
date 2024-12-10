package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery_api.model.DeliveryDto;
import ru.yandex.practicum.delivery_api.model.OrderDto;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryState;
import ru.yandex.practicum.order_api.api.OrderApiClient;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;
import ru.yandex.practicum.warehouse_api.model.ShippedToDeliveryRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    @Qualifier("mvcConversionService")
    private final ConversionService cs;
    private final DeliveryRepository deliveryRepository;
    private final OrderApiClient orderApiClient;
    private final WarehouseApiClient warehouseApiClient;

    private double cost = 5.0;
    private final double COEFFICIENT_FRAGILE = 0.2;
    private final double COEFFICIENT_WEIGHT = 0.3;
    private final double COEFFICIENT_VOLUME = 0.2;
    private final double COEFFICIENT_DELIVERY = 0.2;

    @Override
    public BigDecimal deliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new EntityNotFoundException("Delivery", orderDto.getDeliveryId().toString()));

        if (delivery.getFromAddress().getFlat().equals("ADDRESS_1")) {
            cost *= 1;
        } else if (delivery.getFromAddress().getFlat().equals("ADDRESS_2")) {
            cost *= 2;
        }

        if (orderDto.getFragile()) {
            cost = (cost * COEFFICIENT_FRAGILE) + cost;
        }

        cost = cost + (orderDto.getDeliveryWeight() * COEFFICIENT_WEIGHT);
        cost = cost + (orderDto.getDeliveryVolume() * COEFFICIENT_VOLUME);

        if (delivery.getFromAddress().getCountry().equals(delivery.getToAddress().getCountry())
                && delivery.getFromAddress().getCity().equals(delivery.getToAddress().getCity())
                && delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {

            cost = (cost * COEFFICIENT_DELIVERY) + cost;
        }

        return BigDecimal.valueOf(cost);
    }

    @Override
    public Void deliveryFailed(UUID body) {
        Delivery delivery = getDeliveryByOrderId(body);

        delivery.setDeliveryState(DeliveryState.FAILED);

        deliveryRepository.save(delivery);
        log.info("Delivery failed: {}", body);

        orderApiClient.deliveryFailed(body, body);

        return null;
    }

    private Delivery getDeliveryByOrderId(UUID body) {
        return deliveryRepository.findByOrderId(body)
                .orElseThrow(() -> new EntityNotFoundException("Delivery with order id", body.toString()));
    }

    @Override
    public Void deliveryPicked(UUID body) {
        Delivery delivery = deliveryRepository.findById(body)
                .orElseThrow(() -> new EntityNotFoundException("Delivery", body.toString()));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);

        deliveryRepository.save(delivery);
        log.info("Delivery picked: {}", body);

        ShippedToDeliveryRequest shippedToDeliveryRequest = new ShippedToDeliveryRequest();
        shippedToDeliveryRequest.setOrderId(body);
        shippedToDeliveryRequest.setDeliveryId(delivery.getDeliveryId());

        warehouseApiClient.shippedToDelivery(shippedToDeliveryRequest);

        return null;
    }

    @Override
    public Void deliverySuccessful(UUID body) {
        Delivery delivery = getDeliveryByOrderId(body);

        delivery.setDeliveryState(DeliveryState.DELIVERED);

        deliveryRepository.save(delivery);
        log.info("Delivery delivered: {}", body);

        orderApiClient.delivery(body, body);

        return null;
    }

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = cs.convert(deliveryDto, Delivery.class);
        delivery.setDeliveryState(DeliveryState.CREATED);

        deliveryRepository.save(delivery);
        log.info("Delivery has been created {}", delivery);

        deliveryDto.setDeliveryId(delivery.getDeliveryId());

        return deliveryDto;
    }
}
