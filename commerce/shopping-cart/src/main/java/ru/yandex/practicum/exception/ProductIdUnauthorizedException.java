package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ProductIdUnauthorizedException extends ResponseStatusException {
    public ProductIdUnauthorizedException(String productId) {
        super(HttpStatus.UNAUTHORIZED, "Unauthorized content product id : [%s]".formatted(productId));
    }
}
