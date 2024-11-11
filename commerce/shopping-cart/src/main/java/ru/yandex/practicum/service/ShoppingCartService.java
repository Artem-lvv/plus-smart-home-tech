package ru.yandex.practicum.service;

import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {
    ShoppingCartDto addProductToShoppingCart(String username,
                                             Map<String, Long> requestBody,
                                             List<String> products);

    BookedProductsDto bookingProductsFromShoppingCart(String username);

    ProductDto changeProductQuantity(String username,
                                      ChangeProductQuantityRequest changeProductQuantityRequest2,
                                      ChangeProductQuantityRequest changeProductQuantityRequest);

    void deactivateCurrentShoppingCart(String username);

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username,
                                            Map<String, Long> requestBody,
                                            List<String> products);
}
