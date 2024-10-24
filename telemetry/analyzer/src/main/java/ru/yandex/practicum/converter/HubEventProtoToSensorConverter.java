package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.Sensor;

@Component
public class HubEventProtoToSensorConverter implements Converter<HubEventProto, Sensor> {
    @Override
    public Sensor convert(HubEventProto hubEventProto) {
        return Sensor.builder()
                .hubId(hubEventProto.getHubId())
                .id(hubEventProto.getDeviceAdded().getId())
                .build();
    }
}
