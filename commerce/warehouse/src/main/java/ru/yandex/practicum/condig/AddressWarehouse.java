package ru.yandex.practicum.condig;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "address-warehouse")
public class AddressWarehouse {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
