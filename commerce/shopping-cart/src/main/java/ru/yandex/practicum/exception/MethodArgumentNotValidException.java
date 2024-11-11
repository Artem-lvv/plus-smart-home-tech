package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class MethodArgumentNotValidException extends ResponseStatusException {
    public MethodArgumentNotValidException(String field) {
        super(HttpStatus.NOT_FOUND, "Invalid request content. Field : [%s]".formatted(field));
    }
}
