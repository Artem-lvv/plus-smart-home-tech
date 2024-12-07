package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.model.QuantityState;
import ru.yandex.practicum.model.ProductState;

@Component
public class ProductEntityToProductDto implements Converter<Product, ProductDto> {

    @Override
    public ProductDto convert(Product src) {
        return new ProductDto(src.getProductId(),
                src.getProductName(),
                src.getDescription(),
                QuantityState.fromValue(src.getQuantityState().getValue()),
                ProductState.fromValue(src.getProductState().getValue()),
                src.getRating(),
                src.getPrice());
    }
}
