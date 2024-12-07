package ru.yandex.practicum.delegate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse_api.api.WarehouseApi;
import ru.yandex.practicum.warehouse_api.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.AddressDto;
import ru.yandex.practicum.warehouse_api.model.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse_api.model.BookedProductsDto;
import ru.yandex.practicum.warehouse_api.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.ShippedToDeliveryRequest;
import ru.yandex.practicum.warehouse_api.model.ShoppingCartDto;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {
    private final WarehouseService warehouseService;

    @Override
    public ResponseEntity<Void> acceptReturn(Map<String, Long> requestBody) {
        warehouseService.acceptReturn(requestBody);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {
        warehouseService.addProductToWarehouse(addProductToWarehouseRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> assemblyProductForOrderFromShoppingCart(
            AssemblyProductForOrderFromShoppingCartRequest assemblyProductForOrderFromShoppingCartRequest) {

        return new ResponseEntity<>(warehouseService
                .assemblyProductForOrderFromShoppingCart(assemblyProductForOrderFromShoppingCartRequest),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto) {
        return new ResponseEntity<>(warehouseService.bookingProductForShoppingCart(shoppingCartDto),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<BookedProductsDto> checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        BookedProductsDto bookedProducts = warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
        return new ResponseEntity<>(bookedProducts, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        return new ResponseEntity<>(warehouseService.getWarehouseAddress(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest) {
        warehouseService.newProductInWarehouse(newProductInWarehouseRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> shippedToDelivery(ShippedToDeliveryRequest shippedToDeliveryRequest) {
        return new ResponseEntity<>(warehouseService.shippedToDelivery(shippedToDeliveryRequest), HttpStatus.OK);
    }
}
