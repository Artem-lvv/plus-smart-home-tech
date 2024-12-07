package ru.yandex.practicum.service;


import ru.yandex.practicum.shopping_store_api.model.Pageable;
import ru.yandex.practicum.shopping_store_api.model.ProductDto;
import ru.yandex.practicum.shopping_store_api.model.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreService {
    ProductDto createNewProduct(ProductDto productDto);

    ProductDto getProduct(UUID productId);

    List<ProductDto> getProducts(String category, Pageable pageable);

    Boolean removeProductFromStore(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityStateRequest request);

    ProductDto updateProduct(ProductDto productDto);
}
