package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.shoppingCart.ShoppingCartEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {
    Optional<ShoppingCartEntity> findByUsernameAndDeactivatedFalse(String username);

    List<ShoppingCartEntity> findAllByShoppingCartId(UUID shoppingCartId);
}
