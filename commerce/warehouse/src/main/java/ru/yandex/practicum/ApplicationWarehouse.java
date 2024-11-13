package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.condig.AddressWarehouse;

@SpringBootApplication
@EnableConfigurationProperties(AddressWarehouse.class)
public class ApplicationWarehouse {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationWarehouse.class, args);
    }
}