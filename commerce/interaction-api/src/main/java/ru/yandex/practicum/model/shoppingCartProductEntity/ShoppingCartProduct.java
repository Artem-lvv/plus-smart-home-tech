package ru.yandex.practicum.model.shoppingCartProductEntity;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.model.productEntity.Product;
import ru.yandex.practicum.model.shoppingCartEntity.ShoppingCart;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "cart_products")
public class ShoppingCartProduct {

    @EmbeddedId
    private ShoppingCartProductId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("shoppingCartId")
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartProduct that = (ShoppingCartProduct) o;
        return Objects.equals(id, that.id) && Objects.equals(shoppingCart, that.shoppingCart)
                && Objects.equals(product, that.product) && Objects.equals(quantity, that.quantity)
                && Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shoppingCart, product, quantity, addedAt);
    }
}
