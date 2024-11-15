package ru.yandex.practicum.service;

import ru.yandex.practicum.warehouse_api.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.AddressDto;
import ru.yandex.practicum.warehouse_api.model.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse_api.model.BookedProductsDto;
import ru.yandex.practicum.warehouse_api.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface WarehouseService {
    void newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequest);


    AddressDto getWarehouseAddress();

    void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequestBody);

    BookedProductsDto bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto);

    BookedProductsDto assemblyProductForOrderFromShoppingCart(
            AssemblyProductForOrderFromShoppingCartRequest assemblyProductForOrderFromShoppingCartRequest);

    void acceptReturn(Map<String, Long> requestBody, List<Object> products);
}
