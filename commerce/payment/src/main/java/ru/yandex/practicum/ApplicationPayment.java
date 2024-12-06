package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.order_api.api.OrderApiClient;
import ru.yandex.practicum.payment_api.api.PaymentApiClient;
import ru.yandex.practicum.shopping_store_api.api.ShoppingStoreApiClient;

@EnableFeignClients(clients = {PaymentApiClient.class, OrderApiClient.class, ShoppingStoreApiClient.class})
@SpringBootApplication
public class ApplicationPayment {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationPayment.class, args);
    }
}