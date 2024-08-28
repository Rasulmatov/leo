package uz.universes.leotelegrambot.button.replayKeyBoard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonPollType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

public class ReplayMarkap {
    public static ReplyKeyboardMarkup contactUz(){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> list=new ArrayList<>();
        KeyboardRow contact=new KeyboardRow();
        contact.add(KeyboardButton.builder()
                        .text("Kontakt yuborish \uD83D\uDCDE")
                        .requestContact(true)
                .build());
        list.add(contact);
        reply.setKeyboard(list);
        return reply;
    }
    public static ReplyKeyboardMarkup contactRu(){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> list=new ArrayList<>();
        KeyboardRow contact=new KeyboardRow();
        contact.add(KeyboardButton.builder()
                        .text("Отправить контакт \uD83D\uDCDE")
                        .requestContact(true)
                .build());
        list.add(contact);
        reply.setKeyboard(list);
        return reply;
    }
    public  static ReplyKeyboardMarkup menuUz(String chatId){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> list=new ArrayList<>();
        KeyboardRow one=new KeyboardRow();
        one.add(KeyboardButton.builder().text("Bonus \uD83C\uDF81").build());
        KeyboardRow two=new KeyboardRow();
        WebAppInfo webAppInfo=new WebAppInfo();
        webAppInfo.setUrl("https://leo.ravshandev.uz/api/v1/?lang=uz&chat_id="+chatId);
        two.add(KeyboardButton.builder().text("Katalog \uD83D\uDECD").webApp(webAppInfo).build());
        KeyboardRow three=new KeyboardRow();
        three.add(KeyboardButton.builder().text("Mening Akkauntim \uD83D\uDC64").build());
        three.add(KeyboardButton.builder().text("Biz bilan bog'lanish \uD83D\uDCDE✉\uFE0F").build());
        list.add(one);
        list.add(two);
        list.add(three);
        reply.setKeyboard(list);
        return reply;
    }
    public  static ReplyKeyboardMarkup menuRu(String chatId){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> list=new ArrayList<>();
        KeyboardRow one=new KeyboardRow();
        one.add(KeyboardButton.builder().text("Бонус \uD83C\uDF81").build());
        KeyboardRow two=new KeyboardRow();
        WebAppInfo webAppInfo=new WebAppInfo();
        webAppInfo.setUrl("https://leo.ravshandev.uz/api/v1/?lang=ru&chat_id="+chatId);
        two.add(KeyboardButton.builder().text("Каталог \uD83D\uDECD").webApp(webAppInfo).build());
        KeyboardRow three=new KeyboardRow();
        three.add(KeyboardButton.builder().text("Мой аккаунт \uD83D\uDC64").requestPoll(KeyboardButtonPollType.builder().type("Salom").build()).build());
        three.add(KeyboardButton.builder().text("Связаться с нами \uD83D\uDCDE✉\uFE0F").build());
        list.add(one);
        list.add(two);
        list.add(three);
        reply.setKeyboard(list);
        return reply;
    }
    public static ReplyKeyboardRemove removeKeyboard(){
        return new ReplyKeyboardRemove(true);
    }
}
