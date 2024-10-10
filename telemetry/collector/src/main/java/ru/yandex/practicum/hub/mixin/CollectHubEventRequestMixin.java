package ru.yandex.practicum.hub.mixin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.yandex.practicum.model.CollectHubEventRequest;
import ru.yandex.practicum.model.DeviceAddedEvent;
import ru.yandex.practicum.model.DeviceRemovedEvent;
import ru.yandex.practicum.model.ScenarioAddedEvent;
import ru.yandex.practicum.model.ScenarioRemovedEvent;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = CollectHubEventRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
public abstract class CollectHubEventRequestMixin {
}