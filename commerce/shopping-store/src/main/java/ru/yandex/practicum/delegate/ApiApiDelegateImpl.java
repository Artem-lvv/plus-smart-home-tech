//package ru.yandex.practicum.delegate;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.api.ApiApiDelegate;
//import ru.yandex.practicum.model.Pageable;
//import ru.yandex.practicum.model.ProductDto;
//import ru.yandex.practicum.model.SetProductQuantityStateRequest;
//import ru.yandex.practicum.service.ShoppingStoreService;
//
//import java.util.List;
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class ApiApiDelegateImpl implements ApiApiDelegate {
//    ShoppingStoreService shoppingStoreService;
//
//    @Override
//    public ResponseEntity<ProductDto> createNewProduct(ProductDto productDto) {
//        return new ResponseEntity<>(shoppingStoreService.createNewProduct(productDto), HttpStatus.OK);
//    }
//
//    @Override
//    public ResponseEntity<ProductDto> getProduct(UUID productId) {
//        return ApiApiDelegate.super.getProduct(productId);
//    }
//
//    @Override
//    public ResponseEntity<List<ProductDto>> getProducts(String category, Pageable pageable) {
//        return ApiApiDelegate.super.getProducts(category, pageable);
//    }
//
//    @Override
//    public ResponseEntity<Boolean> removeProductFromStore(UUID body) {
//        return ApiApiDelegate.super.removeProductFromStore(body);
//    }
//
//    @Override
//    public ResponseEntity<Boolean> setProductQuantityState(SetProductQuantityStateRequest setProductQuantityStateRequest) {
//        return ApiApiDelegate.super.setProductQuantityState(setProductQuantityStateRequest);
//    }
//
//    @Override
//    public ResponseEntity<ProductDto> updateProduct(ProductDto productDto) {
//        return ApiApiDelegate.super.updateProduct(productDto);
//    }
//}
