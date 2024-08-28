package uz.universes.leotelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.universes.leotelegrambot.sendM.SendMessag;
import uz.universes.leotelegrambot.text.TextUz;

@Service
@RequiredArgsConstructor
public class MenuService {
    private Message message;
    private CallbackQuery callbackQuery;
    public Object controllerService(Update update){
        if (update.hasMessage()){
            this.message=update.getMessage();
            return messageService();
        } else if (update.hasCallbackQuery()) {
            this.message=update.getCallbackQuery().getMessage();
            this.callbackQuery=update.getCallbackQuery();
        }
        return null;
    }

    private Object messageService(){
        if (message.hasText()){
            if (message.getText().equals("/start")){
                return SendMessag.sendM(message.getChatId(), TextUz.menu);
            }
        }
        return null;
    }

    private String userCheckLang(Long chatId){
return null;
    }


}
