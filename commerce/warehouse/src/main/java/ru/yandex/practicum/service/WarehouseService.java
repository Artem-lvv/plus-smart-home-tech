package ru.yandex.practicum.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.ShoppingCartDto;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest2,
                          NewProductInWarehouseRequest newProductInWarehouseRequest);


    AddressDto getWarehouseAddress();

    void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequestBody,
                                               AddProductToWarehouseRequest addProductToWarehouseRequest);

    BookedProductsDto bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto, ShoppingCartDto shoppingCart);
}
