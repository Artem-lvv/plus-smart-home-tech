package ru.yandex.practicum.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.api.ApiApiDelegate;
import ru.yandex.practicum.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.service.WarehouseService;

@Component
@RequiredArgsConstructor
public class ApiApiDelegateImpl implements ApiApiDelegate {
    private final WarehouseService warehouseService;

    @Override
    public ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest2,
                                                      AddProductToWarehouseRequest addProductToWarehouseRequest) {

        warehouseService.addProductToWarehouse(addProductToWarehouseRequest2, addProductToWarehouseRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> assemblyProductForOrderFromShoppingCart(
            AssemblyProductForOrderFromShoppingCartRequest assemblyProductForOrderFromShoppingCartRequest2,
            AssemblyProductForOrderFromShoppingCartRequest assemblyProductForOrderFromShoppingCartRequest) {

        return ApiApiDelegate.super.assemblyProductForOrderFromShoppingCart(assemblyProductForOrderFromShoppingCartRequest2, assemblyProductForOrderFromShoppingCartRequest);
    }

    @Override
    public ResponseEntity<BookedProductsDto> bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto,
                                                                           ShoppingCartDto shoppingCart) {
        return new ResponseEntity<>(warehouseService.bookingProductForShoppingCart(shoppingCartDto, shoppingCart),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        return new ResponseEntity<>(warehouseService.getWarehouseAddress(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest2,
                                                      NewProductInWarehouseRequest newProductInWarehouseRequest) {

        warehouseService.newProductInWarehouse(newProductInWarehouseRequest2, newProductInWarehouseRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
