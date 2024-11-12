package ru.yandex.practicum.repository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductEntity;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProductEntity, ShoppingCartProductId> {

    List<ShoppingCartProductEntity> findAllByShoppingCart_ShoppingCartId(UUID shoppingCartId);

    Optional<ShoppingCartProductEntity> findByShoppingCart_ShoppingCartIdAndId_ProductId(UUID shoppingCartId,
                                                                                         UUID productId);
}
