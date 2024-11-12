package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.exception.ProductIdUnauthorizedException;
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

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Override
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username,
                                                    Map<String, Long> requestBody,
                                                    List<String> products) {

        checkUsername(username);

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

        Map<UUID, ShoppingCartProductEntity> currentCartUuidProductToShopCartProduct =
                getCurrentCartProductsByNewProductUUIDs(requestBody, shoppingCartEntity);

        for (Map.Entry<String, Long> newProducts : requestBody.entrySet()) {
            UUID newProductUUID = UUID.fromString(newProducts.getKey());
            ShoppingCartProductEntity shoppingCartProductEntity = currentCartUuidProductToShopCartProduct
                    .get(newProductUUID);

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
                currentCartUuidProductToShopCartProduct.put(newProductUUID, newShoppingCartProductEntity);
                log.info("Create ShoppingCart product entity: {}", newShoppingCartProductEntity);
            } else {
                shoppingCartProductEntity.setQuantity(shoppingCartProductEntity.getQuantity()
                        + Math.toIntExact(newProducts.getValue()));
                log.info("Update ShoppingCart product entity: {}", shoppingCartProductEntity);
            }
        }

        List<ShoppingCartProductEntity> shoppingCartProductEntities = shoppingCartProductRepository
                .saveAll(currentCartUuidProductToShopCartProduct.values());
        log.info("Save/Update ShoppingCart product entity: {}", shoppingCartProductEntities);

        Map<String, Long> uuidProductToQuantity = currentCartUuidProductToShopCartProduct.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        uuidShoppingCartProductEntityEntry1 -> uuidShoppingCartProductEntityEntry1.getKey().toString(),
                        uuidShoppingCartProductEntityEntry -> Integer.toUnsignedLong(uuidShoppingCartProductEntityEntry
                                .getValue().getQuantity())));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCartEntity.getShoppingCartId(),
                uuidProductToQuantity);
        log.info("Create ShoppingCartDto: {}", shoppingCartDto);

        return shoppingCartDto;
    }

    private Map<UUID, ShoppingCartProductEntity> getCurrentCartProductsByNewProductUUIDs(Map<String, Long> requestBody,
                                                                                         ShoppingCartEntity shoppingCartEntity) {
        List<ShoppingCartProductId> shoppingCartProductIdList = requestBody.keySet()
                .stream()
                .map(productId -> ShoppingCartProductId.builder()
                        .shoppingCartId(shoppingCartEntity.getShoppingCartId())
                        .productId(UUID.fromString(productId))
                        .build())
                .toList();

        return shoppingCartProductRepository
                .findAllById(shoppingCartProductIdList)
                .stream()
                .collect(Collectors.toMap(shoppingCartProductEntity -> shoppingCartProductEntity.getId().getProductId(),
                        shoppingCartProductEntity -> shoppingCartProductEntity));
    }

    @Override
    public BookedProductsDto bookingProductsFromShoppingCart(String username) {

        return null;
    }

    @Override
    public ProductDto changeProductQuantity(String username,
                                            ChangeProductQuantityRequest changeProductQuantityRequest2,
                                            ChangeProductQuantityRequest changeProductQuantityRequest) {

        checkUsername(username);

        Optional<ShoppingCartEntity> shoppingCart = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        Optional<ShoppingCartProductEntity> shoppingCartProductEntity = shoppingCartProductRepository
                .findByShoppingCart_ShoppingCartIdAndId_ProductId(shoppingCart.get().getShoppingCartId(),
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
    public void deactivateCurrentShoppingCart(String username) {
        checkUsername(username);

        Optional<ShoppingCartEntity> shoppingCart = shoppingCartRepository
                .findByUsernameAndDeactivatedFalse(username);

        shoppingCart.get().setDeactivated(true);
        shoppingCartRepository.save(shoppingCart.get());
        log.info("Deactivate ShoppingCart entity: {}", shoppingCart.get());
    }

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        checkUsername(username);

        Optional<ShoppingCartEntity> shoppingCart = shoppingCartRepository.findByUsernameAndDeactivatedFalse(username);
        List<ShoppingCartProductEntity> allByShoppingCartProductByCartId = shoppingCartProductRepository
                .findAllByShoppingCart_ShoppingCartId(shoppingCart.get().getShoppingCartId());

        Map<String, Long> uuidProductToQuantity = allByShoppingCartProductByCartId
                .stream()
                .collect(Collectors.toMap(shoppingCartProductEntity -> shoppingCartProductEntity
                                .getProduct().getProductId().toString(),
                        shoppingCartProductEntity -> Integer.toUnsignedLong(shoppingCartProductEntity.getQuantity())));

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCart.get().getShoppingCartId(),
                uuidProductToQuantity);
        log.info("Create ShoppingCartDto: {}", shoppingCartDto);

        return shoppingCartDto;
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String username,
                                                  Map<String, Long> requestBody,
                                                  List<String> products) {

        checkUsername(username);

        Optional<ShoppingCartEntity> shoppingCart = shoppingCartRepository.findByUsernameAndDeactivatedFalse(username);

        Map<String, ShoppingCartProductEntity> idProductToShopCartProduct = shoppingCartProductRepository
                .findAllByShoppingCart_ShoppingCartId(shoppingCart.get().getShoppingCartId())
                .stream()
                .collect(Collectors.toMap(
                        shoppingCartProductEntity -> shoppingCartProductEntity.getProduct().getProductId().toString(),
                        shoppingCartProductEntity -> shoppingCartProductEntity));

        List<ShoppingCartProductId> deletedList = new ArrayList<>(10);

        requestBody.keySet().forEach(productId -> {
            ShoppingCartProductEntity shoppingCartProductEntity = idProductToShopCartProduct.get(productId);
            if (Objects.nonNull(shoppingCartProductEntity)) {
                deletedList.add(shoppingCartProductEntity.getId());
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

        return new ShoppingCartDto(shoppingCart.get().getShoppingCartId(), collect);
    }
}
