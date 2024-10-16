package ru.yandex.practicum;

import com.google.protobuf.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProtobufConsumer {

    @KafkaListener(topics = "telemetry.sensors.v1", groupId = "aggregator-group")
    public void listen(Message message) {
        System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTt");
        System.out.println("Received data: " + message);
    }

}