package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityNotFoundException extends ResponseStatusException {
    public EntityNotFoundException(String nameEntity, String field) {
        super(HttpStatus.BAD_REQUEST, "No entity [%s]  by field : [%s]".formatted(nameEntity, field));
    }
}
