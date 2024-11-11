package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductEntity;
import ru.yandex.practicum.model.entity.shoppingCartProduct.ShoppingCartProductId;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProductEntity, ShoppingCartProductId> {

}
