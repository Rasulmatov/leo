package uz.universes.leotelegrambot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.universes.leotelegrambot.Model.*;
import uz.universes.leotelegrambot.button.inlline.InlineButtons;
import uz.universes.leotelegrambot.button.replayKeyBoard.ReplayMarkap;
import uz.universes.leotelegrambot.config.Config;
import uz.universes.leotelegrambot.map.BonusSave;
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

import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ControllerBot extends TelegramLongPollingBot {
    private final Config config;
    private final Regis regis;
    private final StepUser stepUser;
    private final BonusSave bonusSave;
    private final UsersMap usersMap;
    private final RequestService requestService;
    List<PhotoSize> list=new LinkedList<>();
    private Message message;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            this.message=update.getMessage();
            if (message.getChatId().equals(-1002157490414L)&&message.getReplyToMessage().getForwardFrom()!=null) {
                executeMessage(CopyMessage.builder()
                        .messageId(message.getMessageId())
                        .fromChatId(message.getChatId())
                        .chatId(message.getReplyToMessage().getForwardFrom().getId())
                        .build()
                );
            }else if (message.hasText()){
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
                    if (bonusSave.getBonusStatus(message.getChatId())) {
                        stepUser.setStep(message.getChatId(), Step.BONUS_COD);
                        executeMessage(SendMessag.sendM(message.getChatId(), TextUz.bonusCod, ReplayMarkap.cancelUz()));
                    }else {
                        executeMessage(SendMessag.sendM(message.getChatId(),"Sizda tekshirilayotgan Promokod bor Admin javobini kuting"));
                    }
                }else if (message.getText().equals("Бонус \uD83C\uDF81")) {
                    if (bonusSave.getBonusStatus(message.getChatId())) {
                        stepUser.setStep(message.getChatId(),Step.BONUS_COD);
                        executeMessage(SendMessag.sendM(message.getChatId(),TextRu.bonusCod,ReplayMarkap.cancelRu()));
                    }else {
                        executeMessage(SendMessag.sendM(message.getChatId(),"У вас есть подтвержденный промокод. Дождитесь ответа администратора"));
                    }

                } else if (message.getText().equals("Bekor qilish ↪\uFE0F")) {
                    stepUser.removeStep(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),"Jarayon bekor qilindi",ReplayMarkap.menuUz(message.getChatId().toString())));
                } else if (message.getText().equals("Отмена ↪\uFE0F")) {
                    stepUser.removeStep(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),"Процесс отменен",ReplayMarkap.menuRu(message.getChatId().toString())));
                }else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.BONUS_COD)){
                    if (message.getText().length()<=10||isAlphaNumericOnly(message.getText())){
                        CheckCod cod=requestService.checkCode(message.getText());
                    if (cod.getSuccess()){
                        stepUser.setStep(message.getChatId(),Step.SEND_PHOTO);
                        bonusSave.setBonusMap(message.getChatId(),message);
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Promo Code Muvaffaqiyatli  ✅ \n\n endi 3 tadan kam bolmagan photo yuboring !"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Промокод успешен ✅\n" +
                                    "\n" +
                                    " Теперь пришлите минимум 3 фотографии!"));
                        }
                    }else {
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), cod.getMessage_uz()+"\uD83D\uDEAB"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),cod.getMessage_ru()+"\uD83D\uDEAB"));
                        }
                    }
                    }else {
                        if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Proma kod mavjud emas \uD83D\uDEAB"));
                        } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                            executeMessage(SendMessag.sendM(message.getChatId(),"Нет доступного промокода \uD83D\uDEAB"));
                        }
                    }
                } else if (message.getText().equals("Связаться с нами \uD83D\uDCDE✉\uFE0F")) {
                    Info info= requestService.info();
                    executeMessage(SendMessag.sendM(
                            message.getChatId(),"Link: "+info.getLink() +"\n" +
                                    "Phone: \n"+info.getPhones()[0].getPhone()+"\n"+info.getPhones()[1].getPhone()
                    ));
                }else if (message.getText().equals("Biz bilan bog'lanish \uD83D\uDCDE✉\uFE0F")){
                    Info info= requestService.info();
                    executeMessage(SendMessag.sendM(
                            message.getChatId(),"Link: "+info.getLink() +"\n" +
                                    "Phone: \n"+info.getPhones()[0].getPhone()+"\n"+info.getPhones()[1].getPhone()
                    ));
                } else if (message.getText().equals("Мой аккаунт \uD83D\uDC64")) {
                    UsersDto usersDto=requestService.getUsers(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),String.format(TextRu.myInfo,usersDto.getPoint(),
                            usersDto.getName(),
                            usersDto.getPhone(),
                            usersDto.getRegion(),
                            usersDto.getLang().toUpperCase()),InlineButtons.editeLang(usersDto.getLang().toLowerCase())));
                }else if (message.getText().equals("Mening Akkauntim \uD83D\uDC64")){
                    UsersDto usersDto=requestService.getUsers(message.getChatId());
                    executeMessage(SendMessag.sendM(message.getChatId(),String.format(TextUz.myInfo,usersDto.getPoint(),
                            usersDto.getName(),
                            usersDto.getPhone(),
                            usersDto.getRegion(),
                            usersDto.getLang().toUpperCase()),InlineButtons.editeLang(usersDto.getLang().toLowerCase())));
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
                bonusSave.setBonusMap(message.getChatId(),message);
                if (bonusSave.getBonusSize(message.getChatId()).size()==4){
                    List<Message> photoList= bonusSave.getBonusSize(message.getChatId());
                    for (int i = 1; i <photoList.size(); i++) {
                        ForwardMessage forwardMessage=new ForwardMessage();
                        forwardMessage.setMessageId(photoList.get(i).getMessageId());
                        forwardMessage.setFromChatId(photoList.get(i).getChatId());
                        forwardMessage.setChatId(-1002157490414L);
                        try {
                            execute(forwardMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    SendMessage sendPhoto=new SendMessage();
                    sendPhoto.setChatId(-1002157490414L);
                    sendPhoto.setReplyMarkup(InlineButtons.checkGroupPhoto(usersMap.getUserDto(message.getChatId()).getLang(),photoList.get(0).getChatId().toString()));
                    stepUser.removeStep(message.getChatId());
                    if (usersMap.getUserDto(photoList.get(0).getChatId()).getLang().equals("uz")) {
                        sendPhoto.setText(TextUz.checkGroupPhoto);
                        executeMessage(SendMessag.sendM(photoList.get(0).getChatId(), "Admin javobini kuting!", ReplayMarkap.menuUz(message.getChatId().toString())));

                    }else if (usersMap.getUserDto(photoList.get(0).getChatId()).getLang().equals("ru")){
                        sendPhoto.setText(TextRu.checkGroupPhoto);
                        executeMessage(SendMessag.sendM(photoList.get(0).getChatId(), "Ждите ответа администратора!", ReplayMarkap.menuRu(message.getChatId().toString())));
                    }
                    try {
                        execute(sendPhoto);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
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
            } else if (update.getCallbackQuery().getData().startsWith("SAVE_PHOTO_")) {
                Long chatId=Long.valueOf(update.getCallbackQuery().getData().split("_")[2]);
               Verify verify= requestService.saveBonus(bonusSave.getBonusSize(chatId).get(0).getText(),Bonus.builder().chat_id(Integer.parseInt(chatId.toString())).build());
               if (usersMap.getUserDto(chatId).getLang().equals("uz")){
                   executeMessage(SendMessag.sendM(chatId,verify.getMessage_uz()+" ✅"));
               } else if (usersMap.getUserDto(chatId).getLang().equals("ru")) {
                   executeMessage(SendMessag.sendM(chatId,verify.getMessage_ru()+" ✅"));
               }
                executeMessage(EditMessageText.builder().text("✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅✅\n" +
                        "#config #"+bonusSave.getBonusSize(chatId).get(0).getText()+"" +
                        "\nTastiqladi: "+update.getCallbackQuery().getFrom().getFirstName()+" "+update.getCallbackQuery().getFrom().getLastName() +"" +
                        "\nMijoz: "+usersMap.getUserDto(chatId).getName()+"" +
                        "\nchatId: #"+chatId).messageId(message.getMessageId()).chatId(message.getChatId()).build());
                bonusSave.deleteBonus(chatId);


            } else if (update.getCallbackQuery().getData().startsWith("NOTSAVE_PHOTO_")) {
                Long chatId=Long.valueOf(update.getCallbackQuery().getData().split("_")[2]);
                if (usersMap.getUserDto(chatId).getLang().equals("uz")) {
                    executeMessage(SendMessag.sendM(chatId, "Bonus xisoblanmadi ❌"));
                } else if (usersMap.getUserDto(chatId).getLang().equals("ru")) {
                    executeMessage(SendMessag.sendM(chatId, "Бонус не засчитывается ❌"));
                }
                executeMessage(EditMessageText.builder().text("❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌❌\n" +
                        "#cancell "+
                        "\nBekor qildi: "+update.getCallbackQuery().getFrom().getFirstName()+" "+update.getCallbackQuery().getFrom().getLastName() +"" +
                        "\nMijoz: "+usersMap.getUserDto(chatId).getName()+"" +
                        "\nchatId: #"+chatId).messageId(message.getMessageId()).chatId(message.getChatId()).build());
                bonusSave.deleteBonus(chatId);
            } else if (update.getCallbackQuery().getData().equals("EDITE_LANG")) {
                stepUser.setStep(message.getChatId(),Step.EDITE_LANG);
                if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")){
                    executeMessage(EditMessageText.builder()
                            .text("Tilni tanlang : ")
                            .chatId(message.getChatId())
                            .messageId(message.getMessageId())
                            .replyMarkup(InlineButtons.lan())
                            .build()
                    );
                }else {
                    executeMessage(EditMessageText.builder()
                            .text("Выберите язык: ")
                            .chatId(message.getChatId())
                            .messageId(message.getMessageId())
                            .replyMarkup(InlineButtons.lan())
                            .build()
                    );
                }
            } else if (!stepUser.getStatus(message.getChatId())&&stepUser.getStep(message.getChatId()).equals(Step.EDITE_LANG)) {
                UsersDto usersDto=requestService.getUsers(message.getChatId());
                stepUser.removeStep(message.getChatId());
                requestService.patchUser(message.getChatId(),UserPatch.builder()
                                .name(usersDto.getName())
                                .chat_id(usersDto.getChat_id())
                                .lang(update.getCallbackQuery().getData())
                                .phone(usersDto.getPhone())
                                .region(1)
                        .build());
                if (update.getCallbackQuery().getData().equals("uz")) {
                    executeMessage(EditMessageText.builder().text(String.format(TextUz.myInfo, usersDto.getPoint(),
                                    usersDto.getName(),
                                    usersDto.getPhone(),
                                    usersDto.getRegion(),
                                    update.getCallbackQuery().getData().toUpperCase()))
                            .messageId(message.getMessageId())
                            .chatId(message.getChatId())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(InlineButtons.editeLang("uz")).build());
                    executeMessage(SendMessag.sendM(message.getChatId(),"O'zbek tiliga o'tildi !! ",ReplayMarkap.menuUz(message.getChatId().toString())));
                } else if (update.getCallbackQuery().getData().equals("ru")) {
                    executeMessage(EditMessageText.builder().text(String.format(TextRu.myInfo, usersDto.getPoint(),
                                    usersDto.getName(),
                                    usersDto.getPhone(),
                                    usersDto.getRegion(),
                                    update.getCallbackQuery().getData().toUpperCase()))
                            .messageId(message.getMessageId())
                            .chatId(message.getChatId())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(InlineButtons.editeLang("ru")).build());
                    executeMessage(SendMessag.sendM(message.getChatId(),"Перешёл на русский!!",ReplayMarkap.menuRu(message.getChatId().toString())));
                }
                usersMap.deleteUser(message.getChatId());
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
            } else if (o instanceof CopyMessage) {
                execute((CopyMessage) o);
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

        return true;
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
