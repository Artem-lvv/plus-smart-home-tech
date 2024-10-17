package ru.yandex.practicum.service;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorService {
    void addSensorsSnapshot(SensorEventProto sensorEventProtoMessage);
}
