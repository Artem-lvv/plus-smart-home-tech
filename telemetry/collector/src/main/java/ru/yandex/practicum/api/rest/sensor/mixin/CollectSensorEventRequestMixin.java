package ru.yandex.practicum.api.rest.sensor.mixin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.yandex.practicum.model.ClimateSensorEvent;
import ru.yandex.practicum.model.CollectSensorEventRequest;
import ru.yandex.practicum.model.LightSensorEvent;
import ru.yandex.practicum.model.MotionSensorEvent;
import ru.yandex.practicum.model.SwitchSensorEvent;
import ru.yandex.practicum.model.TemperatureSensorEvent;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = CollectSensorEventRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT")
})
public class CollectSensorEventRequestMixin {
}
