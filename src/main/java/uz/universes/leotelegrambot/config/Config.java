package uz.universes.leotelegrambot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@PropertySource("application.properties")
public class Config {
    @Value("${telegram.username}")
    private String username;
    @Value(("${telegram.token}"))
    private String token;
}
