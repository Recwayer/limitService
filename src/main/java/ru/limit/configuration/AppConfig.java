package ru.limit.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.limit.configuration.properties.LimitProperties;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LimitProperties.class)
@EnableScheduling
public class AppConfig {
}
