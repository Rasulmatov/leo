package uz.universes.leotelegrambot.map;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class BonusSave {
    private final Map<Long, List<Message>> bonusMap = new HashMap<>();

    public void setBonusMap(Long chatId, Message message) {
        List<Message> list;
        if (getBonusStatus(chatId)){
            list=new ArrayList<>();
            list.add(message);
            bonusMap.put(chatId, (list));
        }else {
            list=bonusMap.get(chatId);
            list.add(message);
            bonusMap.put(chatId,list);
        }
    }

    public List<Message> getBonusSize(Long chatId) {
        return bonusMap.get(chatId);
    }

    public Boolean getBonusStatus(Long chatId) {
        return bonusMap.get(chatId) == null;
    }
    public void deleteBonus(Long chatId){
        bonusMap.remove(chatId);
    }


}
