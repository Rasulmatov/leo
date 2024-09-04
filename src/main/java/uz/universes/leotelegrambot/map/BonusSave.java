package uz.universes.leotelegrambot.map;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PhotoSizeSave {
    private final Map<Long, List<Message>> photoMap = new HashMap<>();

    public void setPhotoMap(Long chatId, Message message) {
        List<Message> list;
        if (getPhotoStatus(chatId)){
            list=new ArrayList<>();
            list.add(message);
            photoMap.put(chatId, (list));
        }else {
            list=photoMap.get(chatId);
            list.add(message);
            photoMap.put(chatId,list);
        }
    }

    public List<Message> getPhotoSize(Long chatId) {
        return photoMap.get(chatId);
    }

    public Boolean getPhotoStatus(Long chatId) {
        return photoMap.get(chatId) == null;
    }
    public void deletePhoto(Long chatId){
        photoMap.remove(chatId);
    }


}
