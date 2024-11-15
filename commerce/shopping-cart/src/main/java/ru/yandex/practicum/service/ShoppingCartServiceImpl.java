package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.exception.ProductIdUnauthorizedException;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.model.shoppingCartEntity.ShoppingCart;
import ru.yandex.practicum.model.shoppingCartProductEntity.ShoppingCartProduct;
import ru.yandex.practicum.model.shoppingCartProductEntity.ShoppingCartProductId;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.ShoppingCartProductRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;
import ru.yandex.practicum.shopping_cart_api.model.BookedProductsDto;
import ru.yandex.practicum.shopping_cart_api.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.shopping_cart_api.model.ProductDto;
import ru.yandex.practicum.shopping_cart_api.model.ShoppingCartDto;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartProductRepository shoppingCartProductRepository;
    private final ProductRepository productRepository;
    private final WarehouseApiClient warehouseApiClient;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<String, Long> requestBody) {

        checkUsername(username);
        ShoppingCart shoppingCart = getShoppingCartEntity(username);

        Map<UUID, Product> uuidToProductEntity = productRepository.findAllByProductIdIn(requestBody.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId,
                        productEntity -> productEntity));

        Map<UUID, ShoppingCartProduct> currentCartUuidProductToShopCartProduct =
                getCurrentCartProductsByNewProductUUIDs(requestBody, shoppingCart);

        for (Map.Entry<String, Long> newProducts : requestBody.entrySet()) {
            UUID newProductUUID = UUID.fromString(newProducts.getKey());
            ShoppingCartProduct shoppingCartProduct = currentCartUuidProductToShopCartProduct
                    .get(newProductUUID);

            if (Objects.isNull(shoppingCartProduct)) {
                ShoppingCartProduct newShoppingCartProduct = ShoppingCartProduct.builder()
                        .id(ShoppingCartProductId.builder()
                                .shoppingCartId(shoppingCart.getShoppingCartId())
                                .productId(newProductUUID)
                                .build())
                        .shoppingCart(shoppingCart)
                        .product(uuidToProductEntity.get(newProductUUID))
                        .quantity(Math.toIntExact(newProducts.getValue()))
                        .addedAt(LocalDateTime.now())
                        .build();
                currentCartUuidProductToShopCartProduct.put(newProductUUID, newShoppingCartProduct);
                log.info("Create ShoppingCart product entity: {}", newShoppingCartProduct);
            } else {
                shoppingCartProduct.setQuantity(shoppingCartProduct.getQuantity()
                        + Math.toIntExact(newProducts.getValue()));
                log.info("Update ShoppingCart product entity: {}", shoppingCartProduct);
            }
        }

        List<ShoppingCartProduct> shoppingCartProductEntities = shoppingCartProductRepository
                .saveAll(currentCartUuidProductToShopCartProduct.values());
        log.info("Save/Update ShoppingCart product entity: {}", shoppingCartProductEntities);

        Map<String, Long> uuidProductToQuantity = currentCartUuidProductToShopCartProduct.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        uuidShoppingCartProductEntityEntry1
                                -> uuidShoppingCartProductEntityEntry1.getKey().toString(),
                        uuidShoppingCartProductEntityEntry
                                -> Integer.toUnsignedLong(uuidShoppingCartProductEntityEntry
                                .getValue().getQuantity())));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCart.getShoppingCartId(),
                uuidProductToQuantity);
        log.info("Create ShoppingCartDto: {}", shoppingCartDto);

        return shoppingCartDto;
    }

    private ShoppingCart getShoppingCartEntity(String username) {
        Optional<ShoppingCart> byUsernameAndDeactivatedFalse = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        ShoppingCart shoppingCart;
        if (byUsernameAndDeactivatedFalse.isPresent()) {
            shoppingCart = byUsernameAndDeactivatedFalse.get();
        } else {
            shoppingCart = ShoppingCart.builder()
                    .username(username)
                    .deactivated(false)
                    .build();
            shoppingCartRepository.save(shoppingCart);
            log.info("Create ShoppingCart entity: {}", shoppingCart);
        }
        return shoppingCart;
    }

    private Map<UUID, ShoppingCartProduct> getCurrentCartProductsByNewProductUUIDs(Map<String, Long> requestBody,
                                                                                   ShoppingCart shoppingCart) {
        List<ShoppingCartProductId> shoppingCartProductIdList = requestBody.keySet()
                .stream()
                .map(productId -> ShoppingCartProductId.builder()
                        .shoppingCartId(shoppingCart.getShoppingCartId())
                        .productId(UUID.fromString(productId))
                        .build())
                .toList();

        return shoppingCartProductRepository
                .findAllById(shoppingCartProductIdList)
                .stream()
                .collect(Collectors.toMap(shoppingCartProduct -> shoppingCartProduct.getId().getProductId(),
                        shoppingCartProduct -> shoppingCartProduct));
    }

    @Override
    public BookedProductsDto bookingProductsFromShoppingCart(String username) {

        ShoppingCartDto shoppingCartDto = getShoppingCart(username);
//        ResponseEntity<BookedProductsDto> bookedProductsDtoResponseEntity = warehouseClient
//                .bookingProductForShoppingCart(shoppingCartDto, shoppingCartDto);

//        ru.yandex.practicum.client.model.ShoppingCartDto shoppingCartDtoClient =
//                new ru.yandex.practicum.client.model.ShoppingCartDto(shoppingCartDto.getShoppingCartId(),
//                        shoppingCartDto.getProducts());

//        ResponseEntity<ru.yandex.practicum.client.model.BookedProductsDto> bookedProductsDtoResponseEntity
//                = apiWarehouseClient.bookingProductForShoppingCart(shoppingCartDtoClient, shoppingCartDtoClient);

//        val bookedProductsDto = warehouseApiClient.bookingProductForShoppingCart(shoppingCartDto);


        return null;
    }

    @Override
    @Transactional
    public ProductDto changeProductQuantity(String username,
                                            ChangeProductQuantityRequest changeProductQuantityRequest) {

        checkUsername(username);

        Optional<ShoppingCart> shoppingCart = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        Optional<ShoppingCartProduct> shoppingCartProductEntity = shoppingCartProductRepository
                .findByShoppingCartIdAndProductId_Fetch(shoppingCart.get().getShoppingCartId(),
                        changeProductQuantityRequest.getProductId());

        if (shoppingCartProductEntity.isEmpty()) {
            throw new ProductIdUnauthorizedException(changeProductQuantityRequest.getProductId().toString());
        }

        shoppingCartProductEntity.get().setQuantity(Math.toIntExact(changeProductQuantityRequest.getNewQuantity()));
        shoppingCartProductRepository.save(shoppingCartProductEntity.get());
        log.info("Update quantity ShoppingCart product entity: {}", shoppingCartProductEntity);

        return cs.convert(shoppingCartProductEntity.get().getProduct(), ProductDto.class);
    }

    private static void checkUsername(String username) {
        if (Objects.isNull(username) || username.isEmpty()) {
            throw new MethodArgumentNotValidException("username");
        }
    }

    @Override
    @Transactional
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);

        Optional<ShoppingCart> shoppingCart = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        shoppingCart.get().setDeactivated(true);
        shoppingCartRepository.save(shoppingCart.get());
        log.info("Deactivate ShoppingCart entity: {}", shoppingCart.get());
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);

        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByUsernameAndDeactivatedFalse(username);
        List<ShoppingCartProduct> allByShoppingCartProductByCartId = shoppingCartProductRepository
                .findAllByShoppingCart_ShoppingCartId_Fetch(shoppingCart.get().getShoppingCartId());

        Map<String, Long> uuidProductToQuantity = allByShoppingCartProductByCartId
                .stream()
                .collect(Collectors.toMap(shoppingCartProduct -> shoppingCartProduct.getProduct()
                                .getProductId().toString(),
                        shoppingCartProduct -> Integer.toUnsignedLong(shoppingCartProduct.getQuantity())));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCart.get().getShoppingCartId(),
                uuidProductToQuantity);
        log.info("Create ShoppingCartDto: {}", shoppingCartDto);

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String username, Map<String, Long> requestBody) {

        checkUsername(username);
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findByUsernameAndDeactivatedFalse(username);

        Map<String, ShoppingCartProduct> idProductToShopCartProduct = shoppingCartProductRepository
                .findAllByShoppingCart_ShoppingCartId_Fetch(shoppingCart.get().getShoppingCartId())
                .stream()
                .collect(Collectors.toMap(
                        shoppingCartProduct -> shoppingCartProduct.getProduct().getProductId().toString(),
                        shoppingCartProduct -> shoppingCartProduct));

        List<ShoppingCartProductId> deletedList = new ArrayList<>(10);

        requestBody.keySet().forEach(productId -> {
            ShoppingCartProduct shoppingCartProduct = idProductToShopCartProduct.get(productId);
            if (Objects.nonNull(shoppingCartProduct)) {
                deletedList.add(shoppingCartProduct.getId());
                idProductToShopCartProduct.remove(productId);
            } else {
                throw new ProductIdUnauthorizedException(productId);
            }
        });

        shoppingCartProductRepository.deleteAllById(deletedList);
        log.info("Remove from ShoppingCartProducts: {}", deletedList);

        Map<String, Long> collect = idProductToShopCartProduct.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Integer.toUnsignedLong(entry.getValue().getQuantity())));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCart.get().getShoppingCartId(), collect);
        log.info("Create hoppingCartDto: {}", shoppingCartDto);

        return shoppingCartDto;
    }
}
