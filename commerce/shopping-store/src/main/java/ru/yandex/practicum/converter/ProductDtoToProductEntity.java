package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.model.productEntity.ProductCategory;
import ru.yandex.practicum.model.productEntity.ProductState;
import ru.yandex.practicum.model.productEntity.QuantityState;
import ru.yandex.practicum.shopping_store_api.model.ProductDto;

import java.util.Objects;

@Component
public class ProductDtoToProductEntity implements Converter<ProductDto, Product> {

    @Override
    public Product convert(ProductDto src) {
        return Product.builder()
                .productId(Objects.isNull(src.getProductId()) ? null : src.getProductId().get())
                .productName(src.getProductName())
                .description(src.getDescription())
                .imageSrc(Objects.isNull(src.getImageSrc()) ? null : src.getImageSrc().get())
                .quantityState(QuantityState.fromValue(src.getQuantityState().getValue()))
                .productState(ProductState.fromValue(src.getProductState().getValue()))
                .rating(src.getRating())
                .productCategory(ProductCategory.fromValue(src.getProductCategory().getValue()))
                .price(src.getPrice())
                .build();
    }
}
