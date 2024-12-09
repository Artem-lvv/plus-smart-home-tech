package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.shopping_store_api.model.ProductDto;
import ru.yandex.practicum.shopping_store_api.model.ProductState;
import ru.yandex.practicum.shopping_store_api.model.QuantityState;

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
