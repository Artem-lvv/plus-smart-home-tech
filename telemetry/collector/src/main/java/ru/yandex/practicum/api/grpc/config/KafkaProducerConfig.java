package ru.yandex.practicum.api.grpc.config;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final Environment environment;

    @Bean
    public KafkaTemplate<String, SpecificRecord> avroKafkaTemplate(ProducerFactory<String,
            SpecificRecord> avroProducerFactory) {
        return new KafkaTemplate<>(avroProducerFactory);
    }

    @Bean
    public ProducerFactory<String, SpecificRecord> avroProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.producer.key-serializer"));
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                environment.getProperty("spring.kafka.avro-producer.value-serializer"));
        configProps.put("schema.registry.url",
                environment.getProperty("spring.kafka.producer.properties.schema.registry.url"));
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, HubEventProto> protobufKafkaTemplate(ProducerFactory<String,
            HubEventProto> protobufProducerFactory) {
        return new KafkaTemplate<>(protobufProducerFactory);
    }

    @Bean
    public ProducerFactory<String, HubEventProto> protobufProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put("bootstrap.servers", environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        configProps.put("key.serializer", environment.getProperty("spring.kafka.producer.key-serializer"));
        configProps.put("value.serializer", environment.getProperty("spring.kafka.protobuf-producer.value-serializer"));
        configProps.put("schema.registry.url",
                environment.getProperty("spring.kafka.protobuf-producer.properties.schema.registry.url"));
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
