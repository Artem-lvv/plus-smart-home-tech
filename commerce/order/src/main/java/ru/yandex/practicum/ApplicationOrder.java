package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.delivery_api.api.DeliveryApiClient;
import ru.yandex.practicum.payment_api.api.PaymentApiClient;
import ru.yandex.practicum.warehouse_api.api.WarehouseApiClient;

@EnableFeignClients(clients = {WarehouseApiClient.class, PaymentApiClient.class, DeliveryApiClient.class})
@SpringBootApplication
public class ApplicationOrder {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationOrder.class, args);
    }
}