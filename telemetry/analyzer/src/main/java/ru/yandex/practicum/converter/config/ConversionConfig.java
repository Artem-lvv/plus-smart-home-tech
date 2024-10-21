package ru.yandex.practicum.converter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import ru.yandex.practicum.converter.DeviceActionProtoToActionConverter;
import ru.yandex.practicum.converter.HubEventProtoToSensorConverter;
import ru.yandex.practicum.converter.ScenarioConditionProtoToConditionConverter;

@Configuration
public class ConversionConfig {

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new HubEventProtoToSensorConverter());
       conversionService.addConverter(new ScenarioConditionProtoToConditionConverter());
        conversionService.addConverter(new DeviceActionProtoToActionConverter());

        return conversionService;
    }

}
