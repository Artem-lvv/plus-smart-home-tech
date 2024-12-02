package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.order_api.api.OrderApiClient;

@EnableFeignClients(clients = {OrderApiClient.class})
@SpringBootApplication
public class ApplicationOrder {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationOrder.class, args);
    }
}