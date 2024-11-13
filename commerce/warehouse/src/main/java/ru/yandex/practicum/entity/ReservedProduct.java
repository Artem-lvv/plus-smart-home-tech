package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "reserved_products")
public class ReservedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reserved_products_id", nullable = false)
    private UUID reservedProductsId;

    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "reserved_quantity")
    private Long reservedQuantity;

}
