package ru.yandex.practicum.in.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.hub.service.HubService;
import ru.yandex.practicum.sensorSnapshot.service.SensorSnapshotService;

@Component
@RequiredArgsConstructor
public class ProtobufConsumer {
    private final SensorSnapshotService sensorSnapshotService;
    private final HubService hubService;

    @KafkaListener(topics = "telemetry.snapshots.v1", groupId = "analyzer-group",
            containerFactory = "avroKafkaListenerContainerFactory")
    public void listenSnapshots(SensorsSnapshotAvro sensorsSnapshotAvro) {
        sensorSnapshotService.processSnapshot(sensorsSnapshotAvro);
    }

    @KafkaListener(topics = "telemetry.hubs.v1", groupId = "analyzer-group",
            containerFactory = "protobufKafkaListenerContainerFactory")
    public void listenHubs(HubEventProto HubEventProto) {
        hubService.processDeviceEvent(HubEventProto);
    }


}