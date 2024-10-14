package ru.yandex.practicum.sensor.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.CollectSensorEventRequest;

public interface SensorEventService {
    ResponseEntity<Void> collectSensorEvent(CollectSensorEventRequest collectSensorEventRequest);
}
