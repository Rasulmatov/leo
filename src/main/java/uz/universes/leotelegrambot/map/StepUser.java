package uz.universes.leotelegrambot.map;

import org.springframework.stereotype.Component;
import uz.universes.leotelegrambot.utils.Step;

import java.util.HashMap;
import java.util.Map;

@Component
public class StepUser {
    private final Map<Long, Step> userStep =new HashMap<>();

    public void setStep(Long chatId, Step step){
        userStep.put(chatId,step);
    }
    public Step getStep(Long chatId){
        return userStep.get(chatId);
    }

    public Boolean getStatus(Long chatId){
        return userStep.get(chatId)==null;
    }
    public void removeStep(Long chatId){
        userStep.remove(chatId);
    }

}
