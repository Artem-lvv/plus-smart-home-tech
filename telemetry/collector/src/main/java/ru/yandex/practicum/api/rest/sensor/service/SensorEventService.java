package ru.yandex.practicum.api.rest.sensor.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.CollectSensorEventRequest;

public interface SensorEventService {
    ResponseEntity<Void> collectSensorEvent(CollectSensorEventRequest collectSensorEventRequest);
}
