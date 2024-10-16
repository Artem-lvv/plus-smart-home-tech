package ru.yandex.practicum.service;

import com.google.protobuf.Message;

public interface SensorService {
    void addSensorsSnapshot(Message message);
}
