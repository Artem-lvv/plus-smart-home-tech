package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.delivery_api.model.DeliveryDto;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.model.DeliveryState;

import java.util.UUID;

@Component
public class DeliveryDtoToDelivery implements Converter<DeliveryDto, Delivery> {
    @Override
    public Delivery convert(DeliveryDto deliveryDto) {
        Address fromAddress = new Address();
        fromAddress.setCountry(deliveryDto.getFromAddress().getCountry());
        fromAddress.setCity(deliveryDto.getFromAddress().getCity());
        fromAddress.setStreet(deliveryDto.getFromAddress().getStreet());
        fromAddress.setHouse(deliveryDto.getFromAddress().getHouse());
        fromAddress.setFlat(deliveryDto.getFromAddress().getFlat());

        Address toAddress = new Address();
        fromAddress.setCountry(deliveryDto.getToAddress().getCountry());
        fromAddress.setCity(deliveryDto.getToAddress().getCity());
        fromAddress.setStreet(deliveryDto.getToAddress().getStreet());
        fromAddress.setHouse(deliveryDto.getToAddress().getHouse());
        fromAddress.setFlat(deliveryDto.getToAddress().getFlat());


        Delivery delivery = new Delivery();
        delivery.setDeliveryState(DeliveryState.valueOf(deliveryDto.getDeliveryState().getValue()));
        delivery.setOrderId(UUID.randomUUID());
        delivery.setFromAddress(fromAddress);
        delivery.setToAddress(toAddress);

        return delivery;
    }
}
