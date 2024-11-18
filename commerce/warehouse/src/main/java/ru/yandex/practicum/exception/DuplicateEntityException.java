package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateEntityException extends ResponseStatusException {
    public DuplicateEntityException(String nameEntity, String uuid) {
        super(HttpStatus.BAD_REQUEST, "Duplicate entity [%s] by field : [%s]".formatted(nameEntity, uuid));
    }
}
