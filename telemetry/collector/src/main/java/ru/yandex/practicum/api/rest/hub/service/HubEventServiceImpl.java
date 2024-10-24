package ru.yandex.practicum.api.rest.hub.service;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.CollectHubEventRequest;

@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Qualifier("mvcConversionService")
    private final ConversionService cs;

    @Value("${collector.topic.telemetry.hubs.v1}")
    private String hubEventTopic;

    @Override
    public ResponseEntity<Void> collectHubEvent(CollectHubEventRequest collectHubEventRequest) {

        HubEventAvro hubEventAvro = cs.convert(collectHubEventRequest, HubEventAvro.class);

       kafkaTemplate.send(hubEventTopic, hubEventAvro);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
