package ru.yandex.practicum.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.ApiApiDelegate;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApiApiDelegateImpl implements ApiApiDelegate {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ResponseEntity<ShoppingCartDto> addProductToShoppingCart(String username,
                                                                    Map<String, Long> requestBody,
                                                                    List<String> products) {

        return new ResponseEntity<>(shoppingCartService.addProductToShoppingCart(username,
                requestBody, products), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> bookingProductsFromShoppingCart(String username) {
        return null;
    }

    @Override
    public ResponseEntity<ProductDto> changeProductQuantity(String username,
                                                            ChangeProductQuantityRequest changeProductQuantityRequest2,
                                                            ChangeProductQuantityRequest changeProductQuantityRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Void> deactivateCurrentShoppingCart(String username) {
        return null;
    }

    @Override
    public ResponseEntity<ShoppingCartDto> getShoppingCart(String username) {
        return null;
    }

    @Override
    public ResponseEntity<ShoppingCartDto> removeFromShoppingCart(String username,
                                                                  Map<String, Long> requestBody,
                                                                  List<String> products) {
        return null;
    }
}
