package uz.universes.leotelegrambot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.net.URI;

@Getter
@PropertySource("application.properties")
@Component
public class RequestUrl {
    @Value("${web.sms}")
    private URI sms;
    @Value("${web.verify}")
    private URI verify;
    @Value("${web.checkUser}")
    private URI checkUser;
    @Value("${web.saveUser}")
    private URI saveUser;
    @Value("${web.getUser}")
    private URI getUser;
    @Value("${web.regions}")
    private URI regions;
    @Value("${web.checkCode}")
    private URI checkCode;


}
