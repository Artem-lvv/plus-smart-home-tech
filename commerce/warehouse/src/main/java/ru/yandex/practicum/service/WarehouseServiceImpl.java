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
import ru.yandex.practicum.entity.model.ProductIdToAvailableStock;
import ru.yandex.practicum.exception.DuplicateEntityException;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.NotEnoughProductInStockException;
import ru.yandex.practicum.repository.ProductInWarehouseRepository;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.ReservedProductRepository;
import ru.yandex.practicum.warehouse_api.model.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.AddressDto;
import ru.yandex.practicum.warehouse_api.model.AssemblyProductForOrderFromShoppingCartRequest;
import ru.yandex.practicum.warehouse_api.model.BookedProductsDto;
import ru.yandex.practicum.warehouse_api.model.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse_api.model.ShoppingCartDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public void newProductInWarehouse(NewProductInWarehouseRequest newProductInWarehouseRequestBody) {

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
    public void addProductToWarehouse(AddProductToWarehouseRequest addProductToWarehouseRequest) {

        Optional<ProductInWarehouse> byProductProductId = productInWarehouseRepository
                .findByProduct_ProductId(addProductToWarehouseRequest.getProductId());

        if (byProductProductId.isEmpty()) {
            log.info("Exception EntityNotFoundException {}", addProductToWarehouseRequest);
            throw new EntityNotFoundException("Product", addProductToWarehouseRequest.getProductId().toString());
        }

        byProductProductId.get().setAvailableStock(byProductProductId.get().getAvailableStock()
                + addProductToWarehouseRequest.getQuantity().intValue());

        productInWarehouseRepository.save(byProductProductId.get());
        log.info("Update field AvailableStock product in warehouse {}", byProductProductId.get());
    }

    @Override
    @Transactional
    public BookedProductsDto bookingProductForShoppingCart(ShoppingCartDto shoppingCartDto) {

        Map<UUID, Long> reservationProducts = shoppingCartDto.getProducts().entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> UUID.fromString(entry.getKey()),
                        Map.Entry::getValue));

        Map<UUID, Long> availableStock = productInWarehouseRepository.findAvailableStock(reservationProducts.keySet()
                        .stream()
                        .toList())
                .stream()
                .collect(Collectors.toMap(ProductIdToAvailableStock::getProductId,
                        ProductIdToAvailableStock::getAvailableStock));

        List<ReservedProduct> toReservation = getReservedProducts(shoppingCartDto, reservationProducts, availableStock);

        reservedProductRepository.saveAll(toReservation);
        log.info("Create ReservedProduct {}", toReservation);

        List<UUID> productIds = toReservation
                .stream()
                .map(ReservedProduct::getProductId)
                .toList();

        Map<UUID, ProductInWarehouse> productIdToPIW = productInWarehouseRepository
                .findAllByProductId_Fetch(productIds)
                .stream()
                .collect(Collectors.toMap(productInWarehouse -> productInWarehouse.getProduct().getProductId(),
                        productInWarehouse -> productInWarehouse));

        return getBookedProductsDto(productIdToPIW, reservationProducts);
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductForOrderFromShoppingCart(
            AssemblyProductForOrderFromShoppingCartRequest assemblyProductForOrderFromShoppingCartRequestBody) {

        UUID shoppingCartId = assemblyProductForOrderFromShoppingCartRequestBody.getShoppingCartId();

        List<ReservedProduct> reservedProductsByShopCartID = reservedProductRepository
                .findAllByShoppingCartId(shoppingCartId);

        List<UUID> productIds = reservedProductsByShopCartID
                .stream()
                .map(ReservedProduct::getProductId)
                .toList();

        Map<UUID, ProductInWarehouse> productInWarehouseMap = productInWarehouseRepository
                .findAllByProductId_Fetch(productIds)
                .stream()
                .collect(Collectors.toMap(productInWarehouse -> productInWarehouse.getProduct().getProductId(),
                        productInWarehouse -> productInWarehouse));

        reservedProductsByShopCartID.forEach(reservedProduct -> {
            ProductInWarehouse productInWarehouse = productInWarehouseMap.get(reservedProduct.getProductId());
            if (Objects.isNull(productInWarehouse)) {
                throw new EntityNotFoundException("Product", reservedProduct.getProductId().toString());
            }
            productInWarehouse.setAvailableStock(productInWarehouse.getAvailableStock()
                    - reservedProduct.getReservedQuantity());
        });

        productInWarehouseRepository.saveAll(productInWarehouseMap.values());
        log.info("Update ProductInWarehouse {}", productInWarehouseMap);

        reservedProductRepository.deleteAll(reservedProductsByShopCartID);
        log.info("Delete ReservedProduct {}", reservedProductsByShopCartID);

        Map<UUID, Long> reservationProducts = reservedProductsByShopCartID.stream()
                .collect(Collectors.toMap(ReservedProduct::getProductId,
                        ReservedProduct::getReservedQuantity));

        return getBookedProductsDto(productInWarehouseMap, reservationProducts);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<String, Long> requestBody, List<Object> products) {
        List<UUID> productIds = requestBody.keySet()
                .stream()
                .map(UUID::fromString)
                .toList();

        Map<UUID, ProductInWarehouse> productInWarehouseMap = productInWarehouseRepository
                .findAllByProductId_Fetch(productIds)
                .stream()
                .collect(Collectors.toMap(productInWarehouse -> productInWarehouse.getProduct().getProductId(),
                        productInWarehouse -> productInWarehouse));

        requestBody.entrySet().forEach(entry -> {
            ProductInWarehouse productInWarehouse = productInWarehouseMap.get(UUID.fromString(entry.getKey()));
            if (Objects.isNull(productInWarehouse)) {
                throw new EntityNotFoundException("Product", entry.getKey());
            }
            productInWarehouse.setAvailableStock(productInWarehouse.getAvailableStock() + entry.getValue());
        });

        productInWarehouseRepository.saveAll(productInWarehouseMap.values());
        log.info("Update ProductInWarehouse {}", productInWarehouseMap);
    }

    private BookedProductsDto getBookedProductsDto(Map<UUID, ProductInWarehouse> productIdToPIW,
                                                   Map<UUID, Long> reservationProducts) {

        AtomicReference<Double> weight = new AtomicReference<>(0.0);
        AtomicReference<Double> volume = new AtomicReference<>(0.0);
        AtomicReference<Boolean> fragile = new AtomicReference<>(false);

        reservationProducts.forEach((productId, quantity) -> {
            ProductInWarehouse productInWarehouse = productIdToPIW.get(productId);
            fragile.set(productInWarehouse.getFragile());
            weight.updateAndGet(v -> v + productInWarehouse.getWeight() * quantity);
            Dimension dimension = productInWarehouse.getDimension();
            volume.updateAndGet(v -> v
                    + (dimension.getHeight() * dimension.getWidth() * dimension.getDepth()) * quantity);
        });

        BookedProductsDto bookedProductsDto = new BookedProductsDto();
        bookedProductsDto.setDeliveryWeight(weight.get());
        bookedProductsDto.setDeliveryVolume(volume.get());
        bookedProductsDto.setFragile(fragile.get());
        log.info("Create BookedProductsDto {}", bookedProductsDto);

        return bookedProductsDto;
    }

    private static List<ReservedProduct> getReservedProducts(ShoppingCartDto shoppingCartDto,
                                                             Map<UUID, Long> reservationProducts,
                                                             Map<UUID, Long> availableStock) {
        List<ReservedProduct> toReservation = new ArrayList<>();

        for (Map.Entry<UUID, Long> entry : reservationProducts.entrySet()) {
            UUID uuidProduct = entry.getKey();
            Long requiredQuantity = entry.getValue();
            Long available = availableStock.get(uuidProduct);

            if (Objects.isNull(available)) {
                throw new EntityNotFoundException("ProductInWarehouse", uuidProduct.toString());
            }

            if (available < requiredQuantity) {
                throw new NotEnoughProductInStockException(uuidProduct.toString(), available - requiredQuantity);
            } else {
                toReservation.add(ReservedProduct.builder()
                        .shoppingCartId(shoppingCartDto.getShoppingCartId())
                        .productId(uuidProduct)
                        .reservedQuantity(requiredQuantity)
                        .build());
            }
        }
        return toReservation;
    }
}
