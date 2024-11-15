package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughProductInStockException extends ResponseStatusException {
    public NotEnoughProductInStockException(String uuid, Long quantity) {
        super(HttpStatus.BAD_REQUEST, "Not enough product in stock [%s] : [%s]".formatted(uuid,
                quantity.toString()));
    }
}
