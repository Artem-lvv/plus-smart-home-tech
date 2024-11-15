package ru.yandex.practicum.service;



import ru.yandex.practicum.shopping_cart_api.model.BookedProductsDto;
import ru.yandex.practicum.shopping_cart_api.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart_api.model.ProductDto;
import ru.yandex.practicum.shopping_cart_api.model.ShoppingCartDto;

import java.util.Map;

public interface ShoppingCartService {
    ShoppingCartDto addProductToShoppingCart(String username, Map<String, Long> requestBody);

    BookedProductsDto bookingProductsFromShoppingCart(String username);

    ProductDto changeProductQuantity(String username, ChangeProductQuantityRequest changeProductQuantityRequest);

    void deactivateCurrentShoppingCart(String username);

    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto removeFromShoppingCart(String username, Map<String, Long> requestBody);
}
