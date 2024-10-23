package ru.yandex.practicum.sensorSnapshot.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SensorSnapshotService {
    void processSnapshot(SensorsSnapshotAvro sensorsSnapshotAvro);
}
