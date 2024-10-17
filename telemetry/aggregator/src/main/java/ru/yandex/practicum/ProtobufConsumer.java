package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.service.SensorService;

@Service
@RequiredArgsConstructor
public class ProtobufConsumer {

    private final SensorService sensorService;

    @KafkaListener(topics = "telemetry.sensors.v1", groupId = "aggregator-group")
    public void listen(SensorEventProto sensorEventProtoMessage) {
        sensorService.addSensorsSnapshot(sensorEventProtoMessage);
    }

}