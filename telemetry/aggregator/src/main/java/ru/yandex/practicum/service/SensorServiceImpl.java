package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorServiceImpl implements SensorService {
    private final Map<String, SensorsSnapshotAvro> hubIdToSensorsSnapshotAvroMap = new HashMap<>(100);
    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;
    private final ConversionService cs;

    @Value("${aggregator.topic.telemetry.snapshots.v1}")
    private String snapshotTopic;

    @Override
    public void addSensorsSnapshot(SensorEventProto sensorEventProtoMessage) {
        if (Objects.isNull(sensorEventProtoMessage)) {
            return;
        }

        Optional<SensorEventAvro> newSensorEventAvro = checkUpdateSensor(sensorEventProtoMessage);
        if (newSensorEventAvro.isEmpty()) return;

        SensorsSnapshotAvro sensorsSnapshotAvro;

        if (!hubIdToSensorsSnapshotAvroMap.containsKey(newSensorEventAvro.get().getHubId())) {
            SensorStateAvro sensorStateAvro = SensorStateAvro.newBuilder()
                    .setTimestamp(newSensorEventAvro.get().getTimestamp())
                    .setData(newSensorEventAvro.get().getPayload())
                    .build();

            Map<String, SensorStateAvro> sensorStateAvroMap = new HashMap<>(100);
            sensorStateAvroMap.put(newSensorEventAvro.get().getId(), sensorStateAvro);

            sensorsSnapshotAvro = SensorsSnapshotAvro.newBuilder()
                    .setHubId(newSensorEventAvro.get().getHubId())
                    .setTimestamp(newSensorEventAvro.get().getTimestamp())
                    .setSensorsState(sensorStateAvroMap)
                    .build();

            log.info("Create snapshot {}", sensorsSnapshotAvro);

            hubIdToSensorsSnapshotAvroMap.put(sensorsSnapshotAvro.getHubId(), sensorsSnapshotAvro);
        } else {
            sensorsSnapshotAvro = hubIdToSensorsSnapshotAvroMap
                    .get(newSensorEventAvro.get().getHubId());
            sensorsSnapshotAvro.getSensorsState().put(newSensorEventAvro.get().getId(), SensorStateAvro.newBuilder()
                    .setTimestamp(newSensorEventAvro.get().getTimestamp())
                    .setData(newSensorEventAvro.get().getPayload())
                    .build());

            log.info("Update snapshot {}", sensorsSnapshotAvro);
        }

        kafkaTemplate.send(snapshotTopic, sensorsSnapshotAvro);
    }

    private Optional<SensorEventAvro> checkUpdateSensor(SensorEventProto sensorEventProtoMessage) {
        if (Objects.isNull(sensorEventProtoMessage)) {
            return Optional.empty();
        }

        String hubId = sensorEventProtoMessage.getHubId();
        String sensorId = sensorEventProtoMessage.getId();

        if (!hubIdToSensorsSnapshotAvroMap.containsKey(hubId)
                || !hubIdToSensorsSnapshotAvroMap.get(hubId).getSensorsState().containsKey(sensorId)) {
            return Optional.ofNullable(cs.convert(sensorEventProtoMessage, SensorEventAvro.class));
        }

        Instant oldTimestampSensor = hubIdToSensorsSnapshotAvroMap.get(hubId)
                .getSensorsState().get(sensorId)
                .getTimestamp();

        Instant newTimestampSensor = Instant.ofEpochSecond(sensorEventProtoMessage.getTimestamp().getSeconds(),
                sensorEventProtoMessage.getTimestamp().getNanos());

        if (oldTimestampSensor.isAfter(newTimestampSensor)) {
            return Optional.empty();
        }

        SensorEventAvro sensorEventAvro = cs.convert(sensorEventProtoMessage, SensorEventAvro.class);

        log.info("Convert sensorEventProtoMessage to sensorEventAvro. Obj sensorEventProtoMessage - {} \n. " +
                "Obj SensorEventAvro - {}", sensorEventProtoMessage, sensorEventAvro);

        return Optional.ofNullable(sensorEventAvro);
    }

}
