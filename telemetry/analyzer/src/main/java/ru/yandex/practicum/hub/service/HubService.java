package ru.yandex.practicum.hub.service;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubService {
    void processDeviceEvent(HubEventProto hubEventProto);
}
