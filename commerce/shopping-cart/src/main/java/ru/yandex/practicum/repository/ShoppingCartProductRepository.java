package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.shoppingCartProductEntity.ShoppingCartProduct;
import ru.yandex.practicum.model.shoppingCartProductEntity.ShoppingCartProductId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, ShoppingCartProductId> {
    @Query("SELECT cp " +
            "FROM ShoppingCartProduct cp " +
            "JOIN FETCH cp.product " +
            "JOIN FETCH cp.shoppingCart " +
            "WHERE cp.shoppingCart.shoppingCartId = :shoppingCardId")
    List<ShoppingCartProduct> findAllByShoppingCart_ShoppingCartId_Fetch(
            @Param("shoppingCardId") UUID shoppingCartId);

    @Query("SELECT cp " +
            "FROM ShoppingCartProduct cp " +
            "JOIN FETCH cp.product " +
            "JOIN FETCH cp.shoppingCart " +
            "WHERE cp.shoppingCart.shoppingCartId = :shoppingCardId " +
            "AND cp.product.productId = :productId")
    Optional<ShoppingCartProduct> findByShoppingCartIdAndProductId_Fetch(
            @Param("shoppingCardId") UUID shoppingCartId,
            @Param("productId") UUID productId);
}
