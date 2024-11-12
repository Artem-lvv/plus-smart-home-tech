package ru.yandex.practicum.model.entity.shoppingCartProduct;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ShoppingCartProductId implements Serializable {
    private UUID shoppingCartId;
    private UUID productId;
}
