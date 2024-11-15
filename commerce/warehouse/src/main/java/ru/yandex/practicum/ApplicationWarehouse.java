package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.condig.AddressWarehouse;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(AddressWarehouse.class)
public class ApplicationWarehouse {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationWarehouse.class, args);
    }
}