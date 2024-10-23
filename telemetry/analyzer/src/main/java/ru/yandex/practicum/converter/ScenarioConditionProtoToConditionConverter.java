package ru.yandex.practicum.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.model.Condition;

@Component
public class ScenarioConditionProtoToConditionConverter implements Converter<ScenarioConditionProto, Condition> {
    @Override
    public Condition convert(ScenarioConditionProto src) {
        return Condition.builder()
                .operation(src.getOperation().name())
                .type(src.getType().name())
                .value(src.getIntValue())
                .build();
    }
}
