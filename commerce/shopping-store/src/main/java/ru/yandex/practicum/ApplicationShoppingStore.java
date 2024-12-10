package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.shopping_store_api.api.ShoppingStoreApi;

@EnableFeignClients(clients = ShoppingStoreApi.class)
@SpringBootApplication
public class ApplicationShoppingStore {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationShoppingStore.class, args);
    }
}