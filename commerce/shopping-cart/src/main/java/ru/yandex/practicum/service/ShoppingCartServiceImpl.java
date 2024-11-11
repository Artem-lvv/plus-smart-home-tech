package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.ChangeProductQuantityRequest;
import ru.yandex.practicum.model.ProductDto;
import ru.yandex.practicum.model.ShoppingCartDto;
import ru.yandex.practicum.model.entity.product.ProductEntity;
import ru.yandex.practicum.model.entity.shoppingCart.ShoppingCartEntity;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductEntity;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductId;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.ShoppingCartProductRepository;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.time.LocalDateTime;
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

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username,
                                                    Map<String, Long> requestBody,
                                                    List<String> products) {

        if (Objects.isNull(username) || username.isEmpty()) {
            throw new MethodArgumentNotValidException("username");
        }

        Optional<ShoppingCartEntity> byUsernameAndDeactivatedFalse = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        ShoppingCartEntity shoppingCartEntity;
        if (byUsernameAndDeactivatedFalse.isPresent()) {
            shoppingCartEntity = byUsernameAndDeactivatedFalse.get();
        } else {
            shoppingCartEntity = ShoppingCartEntity.builder()
                    .username(username)
                    .deactivated(false)
                    .build();
            shoppingCartRepository.save(shoppingCartEntity);
            log.info("Create ShoppingCart entity: {}", shoppingCartEntity);
        }

        Map<UUID, ProductEntity> uuidToProductEntity = productRepository.findAllByProductIdIn(requestBody.keySet())
                .stream()
                .collect(Collectors.toMap(ProductEntity::getProductId,
                        productEntity -> productEntity));

        List<ShoppingCartProductId> shoppingCartProductIdList = requestBody.keySet()
                .stream()
                .map(productId -> ShoppingCartProductId.builder()
                        .shoppingCartId(shoppingCartEntity.getShoppingCartId())
                        .productId(UUID.fromString(productId))
                        .build())
                .toList();

        Map<UUID, ShoppingCartProductEntity> currentProducts = shoppingCartProductRepository
                .findAllById(shoppingCartProductIdList)
                .stream()
                .collect(Collectors.toMap(shoppingCartProductEntity -> shoppingCartProductEntity.getId().getProductId(),
                        shoppingCartProductEntity -> shoppingCartProductEntity));

        for (Map.Entry<String, Long> newProducts : requestBody.entrySet()) {
            UUID newProductUUID = UUID.fromString(newProducts.getKey());
            ShoppingCartProductEntity shoppingCartProductEntity = currentProducts.get(newProductUUID);

            if (Objects.isNull(shoppingCartProductEntity)) {
                ShoppingCartProductEntity newShoppingCartProductEntity = ShoppingCartProductEntity.builder()
                        .id(ShoppingCartProductId.builder()
                                .shoppingCartId(shoppingCartEntity.getShoppingCartId())
                                .productId(newProductUUID)
                                .build())
                        .shoppingCart(shoppingCartEntity)
                        .product(uuidToProductEntity.get(newProductUUID))
                        .quantity(Math.toIntExact(newProducts.getValue()))
                        .addedAt(LocalDateTime.now())
                        .build();
                currentProducts.put(newProductUUID, newShoppingCartProductEntity);
                log.info("Create ShoppingCart product entity: {}", newShoppingCartProductEntity);
            } else {
                shoppingCartProductEntity.setQuantity(shoppingCartProductEntity.getQuantity()
                        + Math.toIntExact(newProducts.getValue()));
                log.info("Update ShoppingCart product entity: {}", shoppingCartProductEntity);
            }
        }

        List<ShoppingCartProductEntity> shoppingCartProductEntities = shoppingCartProductRepository
                .saveAll(currentProducts.values());
        log.info("Save/Update ShoppingCart product entity: {}", shoppingCartProductEntities);

        Map<String, Long> result = currentProducts.entrySet()
                .stream()
                .collect(Collectors.toMap(uuidShoppingCartProductEntityEntry1 -> uuidShoppingCartProductEntityEntry1.
                                getKey().toString(),
                uuidShoppingCartProductEntityEntry -> Integer.toUnsignedLong(uuidShoppingCartProductEntityEntry
                        .getValue().getQuantity())));

        return new ShoppingCartDto(shoppingCartEntity.getShoppingCartId(), result);
    }

    @Override
    public BookedProductsDto bookingProductsFromShoppingCart(String username) {
        return null;
    }

    @Override
    public ProductDto changeProductQuantity(String username,
                                            ChangeProductQuantityRequest changeProductQuantityRequest2,
                                            ChangeProductQuantityRequest changeProductQuantityRequest) {
        return null;
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {

    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        return null;
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username,
                                                  Map<String, Long> requestBody,
                                                  List<String> products) {
        return null;
    }
}
