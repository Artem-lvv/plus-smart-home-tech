package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ProductEntity;

@Component
public class ProductDtoToProductEntity implements Converter<ProductDto, ProductEntity> {

    @Override
    public ProductEntity convert(ProductDto src) {
        return ProductEntity.builder()
                .productId(src.getProductId().get())
                .productName(src.getProductName())
                .description(src.getDescription())
                .imageSrc(src.getImageSrc().get())
                .quantityState(src.getQuantityState())
                .productState(src.getProductState())
                .rating(src.getRating())
                .productCategory(src.getProductCategory())
                .build();
    }
}
