package uz.universes.leotelegrambot.sendM;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class SendMessag {
    public static SendMessage sendM(Long chatId,String text){
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .build();
    }
    public static SendMessage sendM(Long chatId, String text, ReplyKeyboard button){
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(button)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
