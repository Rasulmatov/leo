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
    private URI getUserUz;
    @Value("${web.getUserRu}")
    private URI getUserRU;

    @Value("${web.regions}")
    private URI regions;
    @Value("${web.checkCode}")
    private URI checkCode;
    @Value("${web.infoConnactions}")
    private URI infoConnactions;
    @Value("${web.patchUser}")
    private URI patchUser;
    @Value("${web.bonusSave}")
    private URI bonusSave;

    @Value("${web.katologUz}")
    public  URI katologUz;
    @Value("${web.katologRu}")
    public  URI katologRu;
    @Value("${web.help}")
    public URI help;

}
