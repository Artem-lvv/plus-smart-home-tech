package ru.yandex.practicum.in.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.SensorService;

@Component
@RequiredArgsConstructor
public class ProtobufConsumer {

    private final SensorService sensorService;

    @KafkaListener(topics = "telemetry.sensors.v1", groupId = "aggregator-group")
    public void listen(SensorEventProto sensorEventProtoMessage) {
        sensorService.addSensorsSnapshot(sensorEventProtoMessage);
    }

}