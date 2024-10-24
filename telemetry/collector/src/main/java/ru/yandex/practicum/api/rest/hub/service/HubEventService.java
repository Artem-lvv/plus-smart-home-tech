package ru.yandex.practicum.api.rest.hub.service;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.model.CollectHubEventRequest;

public interface HubEventService {
    ResponseEntity<Void> collectHubEvent(CollectHubEventRequest collectHubEventRequest);
}
