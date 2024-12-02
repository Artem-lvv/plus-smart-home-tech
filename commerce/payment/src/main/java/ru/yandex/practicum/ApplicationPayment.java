package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.payment_api.api.PaymentApiClient;

@EnableFeignClients(clients = {PaymentApiClient.class})
@SpringBootApplication
public class ApplicationPayment {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationPayment.class, args);
    }
}