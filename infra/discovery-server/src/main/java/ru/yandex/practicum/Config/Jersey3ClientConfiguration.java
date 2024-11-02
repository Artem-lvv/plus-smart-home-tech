//package ru.yandex.practicum.Config;
//
//import com.netflix.discovery.AbstractDiscoveryClientOptionalArgs;
//import com.netflix.discovery.Jersey3DiscoveryClientOptionalArgs;
//import com.netflix.discovery.shared.transport.jersey.TransportClientFactories;
//import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.condition.SearchStrategy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConditionalOnClass(name = "org.glassfish.jersey.client.JerseyClient")
//public class Jersey3ClientConfiguration {
//
//    @Bean
//    @ConditionalOnMissingBean(value = {AbstractDiscoveryClientOptionalArgs.class}, search = SearchStrategy.CURRENT)
//    public Jersey3DiscoveryClientOptionalArgs jersey3DiscoveryClientOptionalArgs() {
//        return new Jersey3DiscoveryClientOptionalArgs();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean(value = TransportClientFactories.class, search = SearchStrategy.CURRENT)
//    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
//        return Jersey3TransportClientFactories.getInstance();
//    }
//}