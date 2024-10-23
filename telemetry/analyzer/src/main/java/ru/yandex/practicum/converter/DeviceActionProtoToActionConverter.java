package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.model.Action;

@Component
public class DeviceActionProtoToActionConverter implements Converter<DeviceActionProto, Action> {
    @Override
    public Action convert(DeviceActionProto src) {
        return Action.builder()
                .value(src.getValue())
                .type(src.getType().name())
                .build();
    }
}
