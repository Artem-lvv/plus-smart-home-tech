package ru.yandex.practicum.model.entity.shoppingCartProduct;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.model.entity.product.ProductEntity;
import ru.yandex.practicum.model.entity.shoppingCart.ShoppingCartEntity;

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
public class ShoppingCartProductEntity {

    @EmbeddedId
    private ShoppingCartProductId id;

    @ManyToOne
    @MapsId("shoppingCartId")
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCartEntity shoppingCart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartProductEntity that = (ShoppingCartProductEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(shoppingCart, that.shoppingCart)
                && Objects.equals(product, that.product) && Objects.equals(quantity, that.quantity)
                && Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shoppingCart, product, quantity, addedAt);
    }
}
