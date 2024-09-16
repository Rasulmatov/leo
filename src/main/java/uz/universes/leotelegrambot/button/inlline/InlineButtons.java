package uz.universes.leotelegrambot.button.inlline;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.universes.leotelegrambot.Model.Region;

import java.util.ArrayList;
import java.util.List;

public class InlineButtons {
    public static InlineKeyboardMarkup lan(){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        List<InlineKeyboardButton> lan=new ArrayList<>();
        lan.add(InlineKeyboardButton.builder().text("O'zbek \uD83C\uDDFA\uD83C\uDDFF").callbackData("uz").build());
        lan.add(InlineKeyboardButton.builder().text("Rus \uD83C\uDDF7\uD83C\uDDFA").callbackData("ru").build());
        lists.add(lan);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup goodUz(){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        List<InlineKeyboardButton> lan=new ArrayList<>();
        lan.add(InlineKeyboardButton.builder().text("Tasdiqlash ✅").callbackData("SAVE_").build());
        lists.add(lan);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup checkGroupPhoto(String lang,String chatId){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        List<InlineKeyboardButton> conf=new ArrayList<>();
        List<InlineKeyboardButton> cancel=new ArrayList<>();
        if (lang.equals("uz")) {
            conf.add(InlineKeyboardButton.builder().text("Tasdiqlash ✅").callbackData("SAVE_PHOTO_"+chatId).build());
            cancel.add(InlineKeyboardButton.builder().text("Bonusni bekor qilish ❌").callbackData("NOTSAVE_PHOTO_"+chatId).build());
        } else if (lang.equals("ru")) {
            conf.add(InlineKeyboardButton.builder().text("Подтверждение ✅").callbackData("SAVE_PHOTO_"+chatId).build());
            cancel.add(InlineKeyboardButton.builder().text("Отмена бонуса ❌").callbackData("NOTSAVE_PHOTO_"+chatId).build());
        }
        lists.add(conf);
        lists.add(cancel);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup gooRu(){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        List<InlineKeyboardButton> lan=new ArrayList<>();
        lan.add(InlineKeyboardButton.builder().text("Подтвердить ✅").callbackData("SAVE_").build());
        lists.add(lan);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }
    public static InlineKeyboardMarkup editeLang(String lang){
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        List<InlineKeyboardButton> lan=new ArrayList<>();
        if (lang.equals("uz")) {
            lan.add(InlineKeyboardButton.builder().text("Tilni o'zgartrish ✏\uFE0F").callbackData("EDITE_LANG").build());
        }else if (lang.equals("ru")) {
            lan.add(InlineKeyboardButton.builder().text("Изменить язык ✏\uFE0F").callbackData("EDITE_LANG").build());
        }
        lists.add(lan);
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }
    public static ReplyKeyboard region(List<Region> regions, String lang){
        InlineKeyboardMarkup inline=new InlineKeyboardMarkup();
        List<InlineKeyboardButton> list=null;
        List<List<InlineKeyboardButton>> lists=new ArrayList<>();
        for (int i = 0; i <regions.size(); i++) {
           list =new ArrayList<>();
            for (int j = i; j <i+2 ; j++) {
                if (j<regions.size()){
                    if (lang.equals("uz")) {
                        list.add(InlineKeyboardButton.builder().text(regions.get(j).getName_uz()).callbackData(regions.get(j).getId().toString()+"_"+regions.get(j).getName_uz()).build());
                    } else if (lang.equals("ru")) {
                        list.add(InlineKeyboardButton.builder().text(regions.get(j).getName_ru()).callbackData(regions.get(j).getId().toString()+"_"+regions.get(j).getName_ru()).build());
                    }
                }
            }
            lists.add(list);
            i++;
        }
        inline.setKeyboard(lists);
        return inline;
    }
}
