package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.delivery_api.api.DeliveryApiClient;
import ru.yandex.practicum.order_api.api.OrderApiClient;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;

@EnableFeignClients(clients = {DeliveryApiClient.class, OrderApiClient.class, WarehouseApiClient.class})
@SpringBootApplication
public class ApplicationDelivery {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationDelivery.class, args);
    }
}