package ru.yandex.practicum.api.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.CollectHubEventRequest;
import ru.yandex.practicum.model.DeviceAction;
import ru.yandex.practicum.model.DeviceAddedEvent;
import ru.yandex.practicum.model.DeviceRemovedEvent;
import ru.yandex.practicum.model.ScenarioAddedEvent;
import ru.yandex.practicum.model.ScenarioCondition;
import ru.yandex.practicum.model.ScenarioRemovedEvent;

import java.util.List;

@Component
public class CollectHubEventRequestToAvroConverter implements Converter<CollectHubEventRequest, HubEventAvro> {

    @Override
    public HubEventAvro convert(CollectHubEventRequest collectHubEventRequest) {

        return switch (collectHubEventRequest) {
            case DeviceAddedEvent deviceAddedEvent -> convertDeviceAddedEventToAvro(deviceAddedEvent);
            case DeviceRemovedEvent deviceRemovedEvent -> convertDeviceRemovedEventToAvro(deviceRemovedEvent);
            case ScenarioAddedEvent scenarioAddedEvent -> convertScenarioAddedEventToAvro(scenarioAddedEvent);
            case ScenarioRemovedEvent scenarioRemovedEvent -> convertScenarioRemovedEventToAvro(scenarioRemovedEvent);
            default -> null;
        };

    }

    private HubEventAvro convertDeviceAddedEventToAvro(DeviceAddedEvent deviceAddedEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(deviceAddedEvent.getHubId())
                .setTimestamp(deviceAddedEvent.getTimestamp().toInstant())
                .setPayload(DeviceAddedEventAvro.newBuilder()
                        .setId(deviceAddedEvent.getId())
                        .setType(DeviceTypeAvro.MOTION_SENSOR)
                        .build())
                .build();
    }

    private HubEventAvro convertDeviceRemovedEventToAvro(DeviceRemovedEvent deviceRemovedEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(deviceRemovedEvent.getHubId())
                .setTimestamp(deviceRemovedEvent.getTimestamp().toInstant())
                .setPayload(DeviceRemovedEventAvro.newBuilder()
                        .setId(deviceRemovedEvent.getId())
                        .build())
                .build();
    }


    private HubEventAvro convertScenarioAddedEventToAvro(ScenarioAddedEvent scenarioAddedEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(scenarioAddedEvent.getHubId())
                .setTimestamp(scenarioAddedEvent.getTimestamp().toInstant())
                .setPayload(ScenarioAddedEventAvro.newBuilder()
                        .setName(scenarioAddedEvent.getName())
                        .setConditions(convertConditions(scenarioAddedEvent.getConditions()))
                        .setActions(convertActions(scenarioAddedEvent.getActions()))
                        .build())
                .build();

    }

    private HubEventAvro convertScenarioRemovedEventToAvro(ScenarioRemovedEvent scenarioRemovedEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(scenarioRemovedEvent.getHubId())
                .setTimestamp(scenarioRemovedEvent.getTimestamp().toInstant())
                .setPayload(ScenarioRemovedEventAvro.newBuilder()
                        .setName(scenarioRemovedEvent.getName())
                        .build())
                .build();
    }

    private List<ScenarioConditionAvro> convertConditions(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(condition.getType().getValue()))
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().getValue()))
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }


    private List<DeviceActionAvro> convertActions(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().getValue()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }

}


