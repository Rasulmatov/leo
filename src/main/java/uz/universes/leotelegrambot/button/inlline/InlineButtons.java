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
        lan.add(InlineKeyboardButton.builder().text("Tastiqlash ✅").callbackData("SAVE_").build());
        lists.add(lan);
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
