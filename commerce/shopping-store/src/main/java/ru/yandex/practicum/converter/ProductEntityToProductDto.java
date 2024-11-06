package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ProductEntity;

@Component
public class ProductEntityToProductDto implements Converter<ProductEntity, ProductDto> {

    @Override
    public ProductDto convert(ProductEntity src) {
        return new ProductDto(src.getProductId(),
                src.getProductName(),
                src.getDescription(),
                src.getQuantityState(),
                src.getProductState(),
                src.getRating(),
                src.getPrice());
    }
}
