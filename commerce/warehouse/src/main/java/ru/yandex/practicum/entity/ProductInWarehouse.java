package ru.yandex.practicum.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.model.entity.product.Product;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "product_in_warehouse")
public class ProductInWarehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_in_warehouse_id", nullable = false)
    private UUID productInWarehouseId;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "fragile", nullable = false)
    private Boolean fragile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dimension_id", referencedColumnName = "dimension_id", nullable = false)
    private Dimension dimension;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock = 0;
}