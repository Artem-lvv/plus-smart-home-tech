package ru.yandex.practicum.api.grpc.config;

import com.google.protobuf.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerProtoConfig {

    private final Environment environment;

    @Bean
    public KafkaTemplate<String, Message> protobufKafkaTemplate(ProducerFactory<String,
            Message> protobufProducerFactory) {
        return new KafkaTemplate<>(protobufProducerFactory);
    }

    @Bean
    public ProducerFactory<String, Message> protobufProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put("bootstrap.servers", environment.getProperty("spring.kafka.producer.bootstrap-servers"));
        configProps.put("key.serializer", environment.getProperty("spring.kafka.producer.key-serializer"));
        configProps.put("value.serializer", environment.getProperty("spring.kafka.protobuf-producer.value-serializer"));
        configProps.put("schema.registry.url",
                environment.getProperty("spring.kafka.protobuf-producer.properties.schema.registry.url"));
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
