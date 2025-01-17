package uz.universes.leotelegrambot.button.replayKeyBoard;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

public class ReplayMarkap {
    //static String  webUz="https://leobonus.uz/api/v1/?lang=ru&chat_id=";
    static String  webUz="https://web-app.leobonus.uz/?lang=uz&chatId=";
    //static String webRu="https://leobonus.uz/api/v1/?lang=uz&chat_id=";
    static String webRu="https://web-app.leobonus.uz/?lang=ru&chatId=";

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
        webAppInfo.setUrl(webUz+chatId);
        two.add(KeyboardButton.builder().text("Katalog \uD83D\uDECD").webApp(webAppInfo).build());
        KeyboardRow three=new KeyboardRow();
        KeyboardRow fo=new KeyboardRow();
        fo.add(KeyboardButton.builder().text("Qo'llanma \uD83D\uDCD2").build());
        three.add(KeyboardButton.builder().text("Mening Akkauntim \uD83D\uDC64").build());
        three.add(KeyboardButton.builder().text("Biz bilan bog'lanish \uD83D\uDCDE✉\uFE0F").build());
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(fo);
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
        webAppInfo.setUrl(webRu+chatId);
        two.add(KeyboardButton.builder().text("Каталог \uD83D\uDECD").webApp(webAppInfo).build());
        KeyboardRow three=new KeyboardRow();
        KeyboardRow fo=new KeyboardRow();
        fo.add(KeyboardButton.builder().text("Руководство \uD83D\uDCD2").build());
        three.add(KeyboardButton.builder().text("Мой аккаунт \uD83D\uDC64").build());
        three.add(KeyboardButton.builder().text("Связаться с нами \uD83D\uDCDE✉\uFE0F").build());
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(fo);
        reply.setKeyboard(list);
        return reply;
    }
    public static ReplyKeyboardMarkup cancelUz(){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> one=new ArrayList<>();
        KeyboardRow cancel=new KeyboardRow();
        cancel.add(KeyboardButton.builder().text("Bekor qilish ↪\uFE0F").build());
        one.add(cancel);
        reply.setKeyboard(one);
        return  reply;
    }
    public static ReplyKeyboardMarkup cancelRu(){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> one=new ArrayList<>();
        KeyboardRow cancel=new KeyboardRow();
        cancel.add(KeyboardButton.builder().text("Отмена ↪\uFE0F").build());
        one.add(cancel);
        reply.setKeyboard(one);
        return  reply;
    }
    public static ReplyKeyboardMarkup helpRu(){
        ReplyKeyboardMarkup reply=new ReplyKeyboardMarkup();
        reply.setResizeKeyboard(true);
        List<KeyboardRow> one=new ArrayList<>();
        KeyboardRow cancel=new KeyboardRow();
        cancel.add(KeyboardButton.builder().text("Руководство \uD83D\uDCD2").build());
        one.add(cancel);
        reply.setKeyboard(one);
        return  reply;
    }
    public static ReplyKeyboardMarkup helpUz(){
        ReplyKeyboardMarkup helpUz=new ReplyKeyboardMarkup();
        helpUz.setResizeKeyboard(true);
        List<KeyboardRow> one=new ArrayList<>();
        KeyboardRow cancel=new KeyboardRow();
        cancel.add(KeyboardButton.builder().text("Qo'llanma \uD83D\uDCD2").build());
        one.add(cancel);
        helpUz.setKeyboard(one);
        return  helpUz;
    }
    public static ReplyKeyboardRemove removeKeyboard(){
        return new ReplyKeyboardRemove(true);
    }
}
