package ru.yandex.practicum.api.rest.sensor.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.ClimateSensorEvent;
import ru.yandex.practicum.model.CollectSensorEventRequest;
import ru.yandex.practicum.model.LightSensorEvent;
import ru.yandex.practicum.model.MotionSensorEvent;
import ru.yandex.practicum.model.SwitchSensorEvent;
import ru.yandex.practicum.model.TemperatureSensorEvent;

@Component
public class CollectSensorEventRequestToAvroConverter implements Converter<CollectSensorEventRequest, SensorEventAvro> {
    @Override
    public SensorEventAvro convert(CollectSensorEventRequest collectSensorEventRequest) {
        return switch (collectSensorEventRequest) {
            case ClimateSensorEvent climateSensorEvent -> convertClimateSensorEventToAvro(climateSensorEvent);
            case LightSensorEvent lightSensorEvent -> convertLightSensorEventToAvro(lightSensorEvent);
            case MotionSensorEvent motionSensorEvent -> convertMotionSensorEventToAvro(motionSensorEvent);
            case SwitchSensorEvent switchSensorEvent -> convertSwitchSensorEventToAvro(switchSensorEvent);
            case TemperatureSensorEvent temperatureSensorEvent ->
                    convertTemperatureSensorEventToAvro(temperatureSensorEvent);
            default -> null;
        };
    }

    private SensorEventAvro convertClimateSensorEventToAvro(ClimateSensorEvent climateSensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(climateSensorEvent.getId())
                .setHubId(climateSensorEvent.getHubId())
                .setTimestamp(climateSensorEvent.getTimestamp().toInstant())
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setCo2Level(climateSensorEvent.getCo2Level())
                        .setHumidity(climateSensorEvent.getHumidity())
                        .setTemperatureC(climateSensorEvent.getTemperatureC()))
                .build();
    }

    private SensorEventAvro convertLightSensorEventToAvro(LightSensorEvent lightSensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(lightSensorEvent.getId())
                .setHubId(lightSensorEvent.getHubId())
                .setTimestamp(lightSensorEvent.getTimestamp().toInstant())
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensorEvent.getLinkQuality())
                        .setLuminosity(lightSensorEvent.getLuminosity())
                        .build())
                .build();

    }

    private SensorEventAvro convertMotionSensorEventToAvro(MotionSensorEvent motionSensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(motionSensorEvent.getId())
                .setHubId(motionSensorEvent.getHubId())
                .setTimestamp(motionSensorEvent.getTimestamp().toInstant())
                .setPayload(MotionSensorAvro.newBuilder()
                        .setMotion(motionSensorEvent.getMotion())
                        .setLinkQuality(motionSensorEvent.getLinkQuality())
                        .setVoltage(motionSensorEvent.getVoltage())
                        .build())
                .build();
    }

    private SensorEventAvro convertSwitchSensorEventToAvro(SwitchSensorEvent switchSensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(switchSensorEvent.getId())
                .setHubId(switchSensorEvent.getHubId())
                .setTimestamp(switchSensorEvent.getTimestamp().toInstant())
                .setPayload(SwitchSensorAvro.newBuilder()
                        .setState(switchSensorEvent.getState())
                        .build())
                .build();
    }

    private SensorEventAvro convertTemperatureSensorEventToAvro(TemperatureSensorEvent temperatureSensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(temperatureSensorEvent.getId())
                .setHubId(temperatureSensorEvent.getHubId())
                .setTimestamp(temperatureSensorEvent.getTimestamp().toInstant())
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setId(temperatureSensorEvent.getId())
                        .setHubId(temperatureSensorEvent.getHubId())
                        .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                        .setTimestamp(temperatureSensorEvent.getTimestamp().toInstant())
                        .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                        .build())
                .build();
    }

}
