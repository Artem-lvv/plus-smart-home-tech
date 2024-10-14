package ru.yandex.practicum.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.hub.mixin.CollectHubEventRequestMixin;
import ru.yandex.practicum.model.CollectHubEventRequest;
import ru.yandex.practicum.model.CollectSensorEventRequest;
import ru.yandex.practicum.sensor.mixin.CollectSensorEventRequestMixin;

import java.util.Map;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addMixIns() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.mixIns(
                Map.of(CollectHubEventRequest.class, CollectHubEventRequestMixin.class,
                        CollectSensorEventRequest.class, CollectSensorEventRequestMixin.class));
    }
}