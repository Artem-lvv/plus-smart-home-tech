package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.entity.Dimension;
import ru.yandex.practicum.entity.ProductInWarehouse;
import ru.yandex.practicum.warehouse_api.model.NewProductInWarehouseRequest;

@Component
public class NewProductInWarehouseRequestToEntityConverter implements Converter<NewProductInWarehouseRequest,
        ProductInWarehouse> {

    @Override
    public ProductInWarehouse convert(NewProductInWarehouseRequest source) {
        return ProductInWarehouse.builder()
                .fragile(source.getFragile())
                .dimension(Dimension.builder()
                        .width(source.getDimension().getWidth())
                        .height(source.getDimension().getHeight())
                        .depth(source.getDimension().getDepth())
                        .build())
                .weight(source.getWeight())
                .build();
    }
}
