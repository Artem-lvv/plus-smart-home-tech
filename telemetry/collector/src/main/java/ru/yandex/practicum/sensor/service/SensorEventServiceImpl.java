package ru.yandex.practicum.sensor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.CollectSensorEventRequest;

@Service
@RequiredArgsConstructor
public class SensorEventServiceImpl implements SensorEventService {

    private final KafkaTemplate<String, SensorEventAvro> kafkaTemplate;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Value("${collector.topic.telemetry.sensors.v1}")
    private String sensorTopic;

    @Override
    public ResponseEntity<Void> collectSensorEvent(CollectSensorEventRequest collectSensorEventRequest) {

        final SensorEventAvro sensorEventAvro = cs.convert(collectSensorEventRequest, SensorEventAvro.class);

        kafkaTemplate.send(sensorTopic, sensorEventAvro);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
