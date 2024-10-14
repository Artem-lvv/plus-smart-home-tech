package ru.yandex.practicum.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.hub.service.HubEventService;
import ru.yandex.practicum.model.CollectHubEventRequest;
import ru.yandex.practicum.model.CollectSensorEventRequest;
import ru.yandex.practicum.sensor.service.SensorEventService;

@Component
@RequiredArgsConstructor
public class EventsApiDelegateImpl implements EventsApiDelegate {

    private final HubEventService hubEventService;
    private final SensorEventService sensorEventService;

    @Override
    public ResponseEntity<Void> collectHubEvent(CollectHubEventRequest collectHubEventRequest) {
        return hubEventService.collectHubEvent(collectHubEventRequest);
    }

    @Override
    public ResponseEntity<Void> collectSensorEvent(CollectSensorEventRequest collectSensorEventRequest) {
        return sensorEventService.collectSensorEvent(collectSensorEventRequest);
    }
}
