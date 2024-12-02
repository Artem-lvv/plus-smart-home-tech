package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.productEntity.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}