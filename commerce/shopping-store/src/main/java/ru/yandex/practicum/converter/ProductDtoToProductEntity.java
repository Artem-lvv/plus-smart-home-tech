package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ProductEntity;

import java.util.Objects;

@Component
public class ProductDtoToProductEntity implements Converter<ProductDto, ProductEntity> {

    @Override
    public ProductEntity convert(ProductDto src) {
        return ProductEntity.builder()
                .productId(Objects.isNull(src.getProductId()) ? null : src.getProductId().get())
                .productName(src.getProductName())
                .description(src.getDescription())
                .imageSrc(Objects.isNull(src.getImageSrc()) ? null : src.getImageSrc().get())
                .quantityState(src.getQuantityState())
                .productState(src.getProductState())
                .rating(src.getRating())
                .productCategory(src.getProductCategory())
                .price(src.getPrice())
                .build();
    }
}
