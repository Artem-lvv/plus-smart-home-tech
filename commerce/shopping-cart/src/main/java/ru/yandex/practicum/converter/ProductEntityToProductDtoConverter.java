package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ProductState;
import ru.yandex.practicum.model.QuantityState;
import ru.yandex.practicum.model.entity.product.Product;

@Component
public class ProductEntityToProductDtoConverter implements Converter<Product, ProductDto> {
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
