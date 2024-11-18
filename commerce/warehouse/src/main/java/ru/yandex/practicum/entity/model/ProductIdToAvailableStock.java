package ru.yandex.practicum.entity.model;

import java.util.UUID;

public interface ProductIdToAvailableStock {
    UUID getProductId();

    Long getAvailableStock();
}
