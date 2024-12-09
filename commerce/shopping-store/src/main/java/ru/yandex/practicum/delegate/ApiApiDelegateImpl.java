package ru.yandex.practicum.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.shopping_store_api.api.ShoppingStoreApi;
import ru.yandex.practicum.shopping_store_api.model.Pageable;
import ru.yandex.practicum.shopping_store_api.model.ProductDto;
import ru.yandex.practicum.shopping_store_api.model.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApiApiDelegateImpl implements ShoppingStoreApi {
    private final ShoppingStoreService shoppingStoreService;

    @Override
    public ResponseEntity<ProductDto> createNewProduct(ProductDto productDto) {
        return new ResponseEntity<>(shoppingStoreService.createNewProduct(productDto), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> getProduct(UUID productId) {
        return new ResponseEntity<>(shoppingStoreService.getProduct(productId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ProductDto>> getProducts(String category, Pageable pageable) {
        return new ResponseEntity<>(shoppingStoreService.getProducts(category, pageable), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Boolean> removeProductFromStore(UUID body) {
        return new ResponseEntity<>(shoppingStoreService.removeProductFromStore(body), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Boolean> setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
        return new ResponseEntity<>(shoppingStoreService.setProductQuantityState(setProductQuantityStateRequest),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(ProductDto productDto) {
        return new ResponseEntity<>(shoppingStoreService.updateProduct(productDto), HttpStatus.OK);
    }
}
