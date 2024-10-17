package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.time.Instant;

@Component
public class SensorEventProtoToAvroConverter implements Converter<SensorEventProto, SensorEventAvro> {

    @Override
    public SensorEventAvro convert(SensorEventProto source) {
        if (source == null) {
            return null;
        }

        SensorEventAvro.Builder sensorEventAvroBuilder = SensorEventAvro.newBuilder()
                .setId(source.getId())
                .setHubId(source.getHubId())
                .setTimestamp(Instant.ofEpochSecond(source.getTimestamp().getSeconds(),
                        source.getTimestamp().getNanos())); // Преобразование в миллисекунды

        // Обработка поля payload
        switch (source.getPayloadCase()) {
            case MOTION_SENSOR_EVENT:
                MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                        .setLinkQuality(source.getMotionSensorEvent().getLinkQuality())
                        .setMotion(source.getMotionSensorEvent().getMotion())
                        .setVoltage(source.getMotionSensorEvent().getVoltage())
                        .build();
                sensorEventAvroBuilder.setPayload(motionSensorAvro);
                break;
            case TEMPERATURE_SENSOR_EVENT:
                TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                        .setId(source.getId())
                        .setHubId(source.getHubId())
                        .setTimestamp(Instant.ofEpochSecond(source.getTimestamp().getSeconds(),
                                source.getTimestamp().getNanos()))
                        .setTemperatureC(source.getTemperatureSensorEvent().getTemperatureC())
                        .setTemperatureF(source.getTemperatureSensorEvent().getTemperatureF())
                        .build();
                sensorEventAvroBuilder.setPayload(temperatureSensorAvro);
                break;
            case LIGHT_SENSOR_EVENT:
                LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                        .setLinkQuality(source.getLightSensorEvent().getLinkQuality())
                        .setLuminosity(source.getLightSensorEvent().getLuminosity())
                        .build();
                sensorEventAvroBuilder.setPayload(lightSensorAvro);
                break;
            case CLIMATE_SENSOR_EVENT:
                ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                        .setTemperatureC(source.getClimateSensorEvent().getTemperatureC())
                        .setHumidity(source.getClimateSensorEvent().getHumidity())
                        .setCo2Level(source.getClimateSensorEvent().getCo2Level())
                        .build();
                sensorEventAvroBuilder.setPayload(climateSensorAvro);
                break;
            case SWITCH_SENSOR_EVENT:
                SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                        .setState(source.getSwitchSensorEvent().getState())
                        .build();
                sensorEventAvroBuilder.setPayload(switchSensorAvro);
                break;
            case PAYLOAD_NOT_SET:
                // Обработка случая, когда данные не установлены
                break;
        }

        return sensorEventAvroBuilder.build();
    }
}
