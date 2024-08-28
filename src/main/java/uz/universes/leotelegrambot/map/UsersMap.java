package uz.universes.leotelegrambot.map;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpConnection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.universes.leotelegrambot.Model.UsersDto;
import uz.universes.leotelegrambot.service.RequestService;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UsersMap {
    private final RequestService requestService;
    private final Map<Long, UsersDto> listMap=new HashMap<>();
   public UsersDto getUserDto(Long chatId){
       if (userIsEmpty(chatId)){
           listMap.put(chatId,requestService.getUsers(chatId));
       }
       return listMap.get(chatId);
   }

   public Boolean userIsEmpty(Long chatId){
       return listMap.get(chatId)==null;
   }

    @Scheduled(cron = "0 56 23 * 6 ?")
   private void delete(){
       listMap.clear();
   }
}
