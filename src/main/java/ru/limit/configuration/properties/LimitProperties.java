package ru.limit.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app")
public record LimitProperties(BigDecimal defaultLimit) {

}
