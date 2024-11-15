package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.entity.ProductInWarehouse;
import ru.yandex.practicum.entity.model.ProductIdToAvailableStock;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductInWarehouseRepository extends JpaRepository<ProductInWarehouse, UUID> {
    Optional<ProductInWarehouse> findByProduct_ProductId(UUID productId);

    @Query("SELECT pw " +
            "FROM ProductInWarehouse pw " +
            "JOIN FETCH pw.dimension " +
            "JOIN FETCH pw.product " +
            "WHERE pw.product.productId IN :productIds")
    List<ProductInWarehouse> findAllByProductId_Fetch(@Param("productIds") List<UUID> productId);

    @Query("""
        SELECT piw.product.productId as productId, 
               (piw.availableStock - COALESCE(SUM(rp.reservedQuantity), 0)) as availableStock
        FROM ProductInWarehouse piw
        LEFT JOIN ReservedProduct rp ON piw.product.productId = rp.productId
        WHERE piw.product.productId IN :productIds
        GROUP BY piw.product.productId, piw.availableStock
    """)
    List<ProductIdToAvailableStock> findAvailableStock(@Param("productIds") List<UUID> productIds);

//    List<ProductInWarehouse> findAllByProduct_ProductIdIn(Collection<UUID> productIds);
}