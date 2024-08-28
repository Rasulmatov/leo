package uz.universes.leotelegrambot.map;

import org.springframework.stereotype.Component;
import uz.universes.leotelegrambot.Model.UsersDto;

import java.util.HashMap;
import java.util.Map;

@Component
public class Regis {
    private final Map<Long, UsersDto> usersMap=new HashMap<>();

    public void setUsersMap(Long chatId, UsersDto usersDto){
        usersMap.put(chatId, usersDto);
    }

    public UsersDto getMapUsers(Long chatId){
        return usersMap.get(chatId);
    }

    public Boolean getStatus(Long chatId){
        return usersMap.get(chatId)==null;
    }


}
