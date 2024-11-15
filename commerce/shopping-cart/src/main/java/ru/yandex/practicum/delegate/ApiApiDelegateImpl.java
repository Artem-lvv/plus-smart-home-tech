package ru.yandex.practicum.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.ShoppingCartService;
import ru.yandex.practicum.shopping_cart_api.api.ShoppingCartApiDelegate;
import ru.yandex.practicum.shopping_cart_api.model.BookedProductsDto;
import ru.yandex.practicum.shopping_cart_api.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart_api.model.ProductDto;
import ru.yandex.practicum.shopping_cart_api.model.ShoppingCartDto;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiApiDelegateImpl implements ShoppingCartApiDelegate {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(String username, Map<String, Long> requestBody) {

        return new ResponseEntity<>(shoppingCartService.addProductToShoppingCart(username, requestBody), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> bookingProductsFromShoppingCart(String username) {
        return new ResponseEntity<>(shoppingCartService.bookingProductsFromShoppingCart(username), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> changeProductQuantity(String username,
                                                            ChangeProductQuantityRequest changeProductQuantityRequest) {

        return new ResponseEntity<>(shoppingCartService.changeProductQuantity(username,
                changeProductQuantityRequest),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deactivateCurrentShoppingCart(String username) {
        shoppingCartService.deactivateCurrentShoppingCart(username);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ShoppingCartDto> getShoppingCart(String username) {
        return new ResponseEntity<>(shoppingCartService.getShoppingCart(username), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(String username, Map<String, Long> requestBody) {
        return new ResponseEntity<>(shoppingCartService.removeFromShoppingCart(username, requestBody),
                HttpStatus.OK);
    }
}
