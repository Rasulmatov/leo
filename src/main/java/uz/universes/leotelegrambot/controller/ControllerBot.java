package uz.universes.leotelegrambot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.universes.leotelegrambot.Model.*;
import uz.universes.leotelegrambot.button.inlline.InlineButtons;
import uz.universes.leotelegrambot.button.replayKeyBoard.ReplayMarkap;
import uz.universes.leotelegrambot.config.Config;
import uz.universes.leotelegrambot.map.Regis;
import uz.universes.leotelegrambot.map.StepUser;
import uz.universes.leotelegrambot.map.UsersMap;
import uz.universes.leotelegrambot.sendM.SendMessag;
import uz.universes.leotelegrambot.service.RequestService;
import uz.universes.leotelegrambot.text.TextRu;
import uz.universes.leotelegrambot.text.TextUz;
import uz.universes.leotelegrambot.text.regis.RegisTextRu;
import uz.universes.leotelegrambot.text.regis.RegisTextUz;
import uz.universes.leotelegrambot.utils.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ControllerBot extends TelegramLongPollingBot {
    private final Config config;
    private final Regis regis;
    private final StepUser stepUser;
    private final UsersMap usersMap;
    private final RequestService requestService;
    List<PhotoSize> list=new LinkedList<>();
    private Message message;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            this.message=update.getMessage();
            if (message.hasText()){
                if (message.getText().equals("/start")&&stepUser.getStatus(message.getChatId())){
                    if (!requestService.checkUser(message.getChatId())) {
                        stepUser.setStep(message.getChatId(), Step.LANG);
                        executeMessage(SendMessag.sendM(message.getChatId(), TextUz.start + "\n" + TextRu.start, InlineButtons.lan()));
                    }else {
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Leo Botiga xush kelibsiz", ReplayMarkap.menuUz(message.getChatId().toString())));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Добро пожаловать в Лео Бот", ReplayMarkap.menuRu(message.getChatId().toString())));
                        }
                    }
                } else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.NAME)) {
                    stepUser.setStep(message.getChatId(),Step.PHONE);
                    UsersDto usersDto =regis.getMapUsers(message.getChatId());
                    usersDto.setName(message.getText());
                    regis.setUsersMap(message.getChatId(), usersDto);
                    if (usersDto.getLang().equals("uz")){
                        executeMessage(SendMessag.sendM(message.getChatId(),RegisTextUz.phone,ReplayMarkap.contactUz()));
                    } else if (usersDto.getLang().equals("ru")) {
                        executeMessage(SendMessag.sendM(message.getChatId(),RegisTextRu.phone,ReplayMarkap.contactRu()));
                    }
                } else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.PHONE)) {
                    UsersDto usersDto =regis.getMapUsers(message.getChatId());
                    if (message.getText().startsWith("+998")&&message.getText().length()==13){
                        stepUser.setStep(message.getChatId(),Step.CHECK);
                        usersDto.setPhone(message.getText());
                        regis.setUsersMap(message.getChatId(), usersDto);
                        if (usersDto.getLang().equals("uz")){
                        executeMessage(SendMessag.sendM(message.getChatId(),RegisTextUz.checkPhone,ReplayMarkap.removeKeyboard()));
                        } else if (usersDto.getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),RegisTextRu.checkPhone,ReplayMarkap.removeKeyboard()));
                        }
                    }else{
                      if (usersDto.getLang().equals("uz")){
                          executeMessage(SendMessag.sendM(message.getChatId(),RegisTextUz.errorPhone));
                      } else if (usersDto.getLang().equals("ru")){
                          executeMessage(SendMessag.sendM(message.getChatId(),RegisTextRu.errorPhone));
                      }
                    }
                } else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.CHECK)) {
                    UsersDto usersDto = regis.getMapUsers(message.getChatId());
                    if (message.getText().length() == 5) {
                        Verify verify = requestService.verifyPhone(CheckSMS.builder().code(message.getText()).phone(usersDto.getPhone()).build());

                        if (verify.getSuccess()) {
                            stepUser.setStep(message.getChatId(), Step.REGION);
                            if (usersDto.getLang().equals("uz")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Kod Muaffaqiyatli ✅"));
                                executeMessage(SendMessag.sendM(message.getChatId(), "Yashaydigan Viloyatni tanlang            !", InlineButtons.region(requestService.getRegions(), "uz")));
                            } else if (usersDto.getLang().equals("ru")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Код выполнен успешно ✅"));
                                executeMessage(SendMessag.sendM(message.getChatId(), "Выбирайте регион, в котором вы живете!", InlineButtons.region(requestService.getRegions(), "ru")));
                            }
                        } else {
                            if (usersDto.getLang().equals("uz")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Kod xato \uD83D\uDE45\u200D♂\uFE0F",ReplayMarkap.cancelUz()));
                            } else if (usersDto.getLang().equals("ru")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Ошибка кода \uD83D\uDE45\u200D♂\uFE0F",ReplayMarkap.cancelRu()));
                            }
                        }
                    } else {
                        if (usersDto.getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), RegisTextUz.errorCode));
                        } else if (usersDto.getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), RegisTextRu.errorCode));
                        }

                }
                }else if (message.getText().equals("Bonus \uD83C\uDF81")) {
                    stepUser.setStep(message.getChatId(),Step.BONUS_COD);
                    executeMessage(SendMessag.sendM(message.getChatId(),TextUz.bonusCod,ReplayMarkap.cancelUz()));
                }else if (message.getText().equals("Бонус \uD83C\uDF81")) {
                    stepUser.setStep(message.getChatId(),Step.BONUS_COD);
                    executeMessage(SendMessag.sendM(message.getChatId(),TextRu.bonusCod,ReplayMarkap.cancelRu()));
                } else if (message.getText().equals("Bekor qilish ↪\uFE0F")) {
                    stepUser.removeStep(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),"Jarayon bekor qilindi",ReplayMarkap.menuUz(message.getChatId().toString())));
                } else if (message.getText().equals("Отмена ↪\uFE0F")) {
                    stepUser.removeStep(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),"Процесс отменен",ReplayMarkap.menuRu(message.getChatId().toString())));
                }else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.BONUS_COD)){
                    if (message.getText().length()<=10||isAlphaNumericOnly(message.getText())){
                    if (requestService.checkCode(message.getText())){
                        stepUser.setStep(message.getChatId(),Step.SEND_PHOTO);
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Promo Code Muvaffaqiyatli  ✅ \n\n endi photo yuboring"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Промокод успешен ✅\n" +
                                    "\n" +
                                    " отправь фото сейчас"));
                        }
                    }else {
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Proma kod mavjud emas \uD83D\uDEAB"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Нет доступного промокода \uD83D\uDEAB"));
                        }
                    }
                    }else {
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Proma kod mavjud emas \uD83D\uDEAB"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Нет доступного промокода \uD83D\uDEAB"));
                        }
                    }
                }
            } else if (message.hasContact()) {
                stepUser.setStep(message.getChatId(),Step.CHECK);
                UsersDto usersDto =regis.getMapUsers(message.getChatId());
                if (!message.getContact().getPhoneNumber().startsWith("+")){
                    usersDto.setPhone("+"+message.getContact().getPhoneNumber());
                }else {
                    usersDto.setPhone(message.getContact().getPhoneNumber());
                }
                regis.setUsersMap(message.getChatId(), usersDto);
                requestService.sendPhone(usersDto.getPhone());
                if (usersDto.getLang().equals("uz")){
                    executeMessage(SendMessag.sendM(message.getChatId(),RegisTextUz.checkPhone,ReplayMarkap.removeKeyboard()));
                }else if (usersDto.getLang().equals("ru")){
                    executeMessage(SendMessag.sendM(message.getChatId(),RegisTextRu.checkPhone,ReplayMarkap.removeKeyboard()));
                }
            } else if (message.hasPhoto()&&!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.SEND_PHOTO)) {
                list.add(message.getPhoto().get(message.getPhoto().size()-1));
                System.out.println(list.size());
                if (list.size()==3){

                    List<InputMedia> list1 = new ArrayList<>();
                    for (int i = 1; i <list.size(); i++) {
                        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                        inputMediaPhoto.setMedia(list.get(i).getFileId());
                        list1.add(inputMediaPhoto);
                    }

                    SendMediaGroup sendMedia = new SendMediaGroup();
                    sendMedia.setChatId(-1002157490414L);
                    sendMedia.setMedias(list1);

                    SendPhoto sendPhoto=new SendPhoto();
                    sendPhoto.setPhoto(new InputFile(list.get(0).getFileId()));
                    sendPhoto.setChatId(-1002157490414L);
                    sendPhoto.setCaption("Buni tastiqlaysizmi?");
                    sendPhoto.setReplyMarkup(InlineButtons.checkGroupPhoto(usersMap.getUserDto(message.getChatId()).getLang()));
                    if (usersMap.getUserDto(message.getChatId()).equals("uz")) {
                        executeMessage(SendMessag.sendM(message.getChatId(), "Admin javobini kuting!  Uz ", ReplayMarkap.menuUz(message.getChatId().toString())));
                    }else if (usersMap.getUserDto(message.getChatId()).equals("uz")){
                        executeMessage(SendMessag.sendM(message.getChatId(), "Admin javobini kuting! Ru", ReplayMarkap.menuRu(message.getChatId().toString())));
                    }
                    try {
                        execute(sendMedia);
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }





               /* ForwardMessage forwardMessage=new ForwardMessage();
                forwardMessage.setMessageId(message.getMessageId());
                forwardMessage.setFromChatId(message.getChatId());
                forwardMessage.setChatId(-1002157490414L);
                try {
                  //  execute(forwardMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }*/
            }
        } else if (update.hasCallbackQuery()) {
            this.message=update.getCallbackQuery().getMessage();
            if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.LANG)){
                stepUser.setStep(message.getChatId(),Step.NAME);
                regis.setUsersMap(message.getChatId(), UsersDto.builder().chat_id(message.getChatId().toString()).lang(update.getCallbackQuery().getData().toLowerCase()).build());
                if (update.getCallbackQuery().getData().toLowerCase().equals("uz")) {
                    executeMessage(EditMessageText.builder().text(RegisTextUz.nameFull).chatId(message.getChatId()).messageId(message.getMessageId()).build());
                } else if (update.getCallbackQuery().getData().toLowerCase().equals("ru")) {
                    executeMessage(EditMessageText.builder().text(RegisTextRu.nameFull).chatId(message.getChatId()).messageId(message.getMessageId()).build());
                }
            } else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.REGION)) {
                stepUser.removeStep(message.getChatId());
                UsersDto usersDto =regis.getMapUsers(message.getChatId());
                usersDto.setRegion(update.getCallbackQuery().getData().split("_")[0]);
                if (usersDto.getLang().equals("uz")) {
                    executeMessage(EditMessageText.builder()
                            .text(String.format(RegisTextUz.infoUser, usersDto.getName(), usersDto.getPhone(), update.getCallbackQuery().getData().split("_")[1], usersDto.getLang().toUpperCase()))
                            .chatId(message.getChatId())
                            .messageId(message.getMessageId())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(InlineButtons.goodUz())
                            .build()
                    );
                } else if (usersDto.getLang().equals("ru")) {
                    executeMessage(EditMessageText.builder()
                            .text(String.format(RegisTextRu.infoUser, usersDto.getName(), usersDto.getPhone(), update.getCallbackQuery().getData().split("_")[1], usersDto.getLang().toUpperCase()))
                            .chatId(message.getChatId())
                            .messageId(message.getMessageId())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(InlineButtons.gooRu())
                            .build()
                    );
                }
            } else if (update.getCallbackQuery().getData().equals("SAVE_")) {
                UsersDto usersDto =regis.getMapUsers(message.getChatId());
                usersDto.setRegion(usersDto.getRegion().split("_")[0]);
                ObjectMapper objectMapper=new ObjectMapper();
                UserEntity entity=objectMapper.convertValue(usersDto,UserEntity.class);
                requestService.saveUser(message.getChatId(),entity);
                executeMessage(DeleteMessage.builder()
                        .chatId(message.getChatId())
                        .messageId(message.getMessageId())
                        .build());
                if (usersDto.getLang().equals("uz")) {
                    executeMessage(SendMessage.builder()
                            .text("Ma'lumotlar saqlandi ✅")
                            .chatId(message.getChatId())
                            .replyMarkup(ReplayMarkap.menuUz(message.getChatId().toString()))
                            .parseMode(ParseMode.HTML)
                            .build()
                    );
                } else if (usersDto.getLang().equals("ru")) {
                    executeMessage(SendMessage.builder()
                            .text("Данные сохранены ✅")
                            .chatId(message.getChatId())
                            .replyMarkup(ReplayMarkap.menuRu(message.getChatId().toString()))
                            .parseMode(ParseMode.HTML)
                            .build()
                    );
                }
            }
        }
    }
    private void executeMessage(Object o){
        try {
            if (o instanceof SendMessage) {
                execute((SendMessage) o);
            }else if (o instanceof EditMessageText){
                execute((EditMessageText) o);
            }else if (o instanceof DeleteMessage){
                execute((DeleteMessage) o );
            }
        }catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isAlphaNumericOnly(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        if (text.matches(".*[^a-zA-Z0-9].*")) {
            return false;
        }

        return true; // Faqat harf va raqamlar bo'lsa, true qaytaradi
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }
}
