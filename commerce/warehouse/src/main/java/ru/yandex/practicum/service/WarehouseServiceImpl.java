package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.condig.AddressWarehouse;
import ru.yandex.practicum.entity.Dimension;
import ru.yandex.practicum.entity.ProductInWarehouse;
import ru.yandex.practicum.entity.ReservedProduct;
import ru.yandex.practicum.exception.DuplicateEntityException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.NotEnoughProductInStockException;
import ru.yandex.practicum.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.model.AddressDto;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.repository.ProductInWarehouseRepository;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.ReservedProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final ReservedProductRepository reservedProductRepository;
    private final ProductInWarehouseRepository productInWarehouseRepository;
    private final ProductRepository productRepository;
    private final AddressWarehouse addressWarehouse;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequestBody,
                                      NewProductInWarehouseRequest newProductInWarehouseRequest) {

        Optional<ProductInWarehouse> byProductProductId = productInWarehouseRepository
                .findByProduct_ProductId(newProductInWarehouseRequestBody.getProductId());

        if (byProductProductId.isPresent()) {
            log.info("Exception DuplicateEntityException {}", newProductInWarehouseRequestBody);
            throw new DuplicateEntityException("Product", newProductInWarehouseRequestBody.getProductId().toString());
        }

        ProductInWarehouse productInWarehouse = cs.convert(newProductInWarehouseRequestBody, ProductInWarehouse.class);

        productInWarehouse.setProduct(productRepository
                .findById(newProductInWarehouseRequestBody.getProductId()).get());

        productInWarehouseRepository.save(productInWarehouse);
        log.info("New product in warehouse {}", productInWarehouse);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCountry(addressWarehouse.getCountry());
        addressDto.setCity(addressWarehouse.getCity());
        addressDto.setStreet(addressWarehouse.getStreet());
        addressDto.setHouse(addressWarehouse.getHouse());
        addressDto.setFlat(addressWarehouse.getFlat());

        return addressDto;
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequestBody,
                                      AddProductToWarehouseRequest addProductToWarehouseRequest) {

        Optional<ProductInWarehouse> byProductProductId = productInWarehouseRepository
                .findByProduct_ProductId(addProductToWarehouseRequestBody.getProductId());

        if (byProductProductId.isEmpty()) {
            log.info("Exception EntityNotFoundException {}", addProductToWarehouseRequestBody);
            throw new EntityNotFoundException("Product", addProductToWarehouseRequestBody.getProductId().toString());
        }

        byProductProductId.get().setAvailableStock(byProductProductId.get().getAvailableStock()
                + addProductToWarehouseRequestBody.getQuantity().intValue());

        productInWarehouseRepository.save(byProductProductId.get());
        log.info("Update field AvailableStock product in warehouse {}", byProductProductId.get());
    }

    @Override
    @Transactional
    public BookedProductsDto bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto,
                                                           ShoppingCartDto shoppingCart) {

        Map<UUID, Long> reservationProducts = shoppingCartDto.getProducts().entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> UUID.fromString(entry.getKey()),
                        Map.Entry::getValue));

        Map<UUID, Long> availableStock = productInWarehouseRepository.findAvailableStock(reservationProducts.keySet()
                .stream()
                .toList());

        List<ReservedProduct> toReservation = new ArrayList<>();

        reservationProducts.forEach((uuidProduct, requiredQuantity) -> {
            Long available = availableStock.get(uuidProduct);
            if (available < requiredQuantity) {
                throw new NotEnoughProductInStockException(uuidProduct.toString(), available - requiredQuantity);
            } else {
                toReservation.add(ReservedProduct.builder()
                        .shoppingCartId(shoppingCartDto.getShoppingCartId())
                        .productId(uuidProduct)
                        .reservedQuantity(requiredQuantity)
                        .build());
            }
        });

        reservedProductRepository.saveAll(toReservation);
        log.info("Create ReservedProduct {}", toReservation);

        Map<UUID, ProductInWarehouse> productIdToPIW = productInWarehouseRepository
                .findAllByProductId_Fetch(toReservation
                        .stream()
                        .map(ReservedProduct::getProductId)
                        .toList())
                .stream()
                .collect(Collectors.toMap(productInWarehouse -> productInWarehouse.getProduct().getProductId(),
                        productInWarehouse -> productInWarehouse));

        AtomicReference<Double> weight = new AtomicReference<>(0.0);
        AtomicReference<Double> volume = new AtomicReference<>(0.0);
        AtomicReference<Boolean> fragile = new AtomicReference<>(false);

        reservationProducts.forEach((productId, quantity) -> {
            ProductInWarehouse productInWarehouse = productIdToPIW.get(productId);
            fragile.set(productInWarehouse.getFragile());
            weight.updateAndGet(v -> v + productInWarehouse.getWeight() * quantity);
            Dimension dimension = productInWarehouse.getDimension();
            volume.updateAndGet(v -> v + dimension.getHeight() * dimension.getWidth() * dimension.getDepth());
        });

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        bookedProductsDto.setDeliveryWeight(weight.get());
        bookedProductsDto.setDeliveryVolume(volume.get());
        bookedProductsDto.setFragile(fragile.get());

        log.info("Create BookedProductsDto {}", bookedProductsDto);

        return bookedProductsDto;
    }
}
