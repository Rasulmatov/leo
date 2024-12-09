package uz.universes.leotelegrambot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.universes.leotelegrambot.Model.*;
import uz.universes.leotelegrambot.button.inlline.InlineButtons;
import uz.universes.leotelegrambot.button.replayKeyBoard.ReplayMarkap;
import uz.universes.leotelegrambot.config.Config;
import uz.universes.leotelegrambot.config.RequestUrl;
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
import uz.universes.leotelegrambot.utils.TypeMessage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ControllerBot extends TelegramLongPollingBot {
    private final Config config;
    private final Regis regis;
    private final StepUser stepUser;
    private final BonusSave bonusSave;
    private final UsersMap usersMap;
    private final RequestService requestService;
    private final RequestUrl requestUrl;
   private Long groupId=-1002267835441L;
    private Message message;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            this.message=update.getMessage();
            if (message.getChatId().equals(groupId)&&message.getReplyToMessage().getForwardFrom()!=null) {
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
                }
                if (requestService.checkUser(message.getChatId())) {
                    if (message.getText().equals("Bonus \uD83C\uDF81")) {
                        if (bonusSave.getBonusStatus(message.getChatId())) {
                            stepUser.setStep(message.getChatId(), Step.BONUS_COD);
                            executeMessage(SendMessag.sendM(message.getChatId(), TextUz.bonusCod, ReplayMarkap.cancelUz()));
                        } else {
                            executeMessage(SendMessag.sendM(message.getChatId(), "Sizda tekshirilayotgan Promokod bor Admin javobini kuting"));
                        }
                    } else if (message.getText().equals("Бонус \uD83C\uDF81")) {
                        if (bonusSave.getBonusStatus(message.getChatId())) {
                            stepUser.setStep(message.getChatId(), Step.BONUS_COD);
                            executeMessage(SendMessag.sendM(message.getChatId(), TextRu.bonusCod, ReplayMarkap.cancelRu()));
                        } else {
                            executeMessage(SendMessag.sendM(message.getChatId(), "У вас есть подтвержденный промокод. Дождитесь ответа администратора"));
                        }

                    } else if (message.getText().equals("Bekor qilish ↪\uFE0F")) {
                        bonusSave.deleteBonus(message.getChatId());
                        stepUser.removeStep(message.getChatId());
                        executeMessage(SendMessag.sendM(message.getChatId(), "Jarayon bekor qilindi", ReplayMarkap.menuUz(message.getChatId().toString())));
                    } else if (message.getText().equals("Руководство \uD83D\uDCD2")) {
                        Help help=requestService.getHelp(2);
                        if (help!=null){
                            if (help.getMessage_type().equals(TypeMessage.VIDEO.toString())){
                                executeMessage(SendVideo.builder()
                                        .video(new InputFile(help.getFile_id()))
                                        .caption(help.getMessage_text())
                                        .chatId(message.getChatId())
                                        .build()
                                );
                            } else if (help.getMessage_type().equals(TypeMessage.PHOTO.toString())) {
                                executeMessage(SendPhoto.builder()
                                        .photo(new InputFile(help.getFile_id()))
                                        .caption(help.getMessage_text())
                                        .chatId(message.getChatId())
                                        .build()
                                );
                            } else if (help.getMessage_type().equals(TypeMessage.TEXT.toString())) {
                                executeMessage(SendMessage.builder()
                                                .text(help.getMessage_text())
                                                .chatId(message.getChatId())
                                                .build()
                                        );
                            }
                        }
                    } else if (message.getText().equals("Qo'llanma \uD83D\uDCD2")) {
                        Help help=requestService.getHelp(1);
                        if (help!=null){
                            if (help.getMessage_type().equals(TypeMessage.VIDEO.toString())){
                                executeMessage(SendVideo.builder()
                                        .video(new InputFile(help.getFile_id()))
                                        .caption(help.getMessage_text())
                                        .chatId(message.getChatId())
                                        .build()
                                );
                            } else if (help.getMessage_type().equals(TypeMessage.PHOTO.toString())) {
                                executeMessage(SendPhoto.builder()
                                        .photo(new InputFile(help.getFile_id()))
                                        .caption(help.getMessage_text())
                                        .chatId(message.getChatId())
                                        .build()
                                );
                            } else if (help.getMessage_type().equals(TypeMessage.TEXT.toString())) {
                                executeMessage(SendMessage.builder()
                                                .text(help.getMessage_text())
                                                .chatId(message.getChatId())
                                                .build()
                                        );
                            }
                        }
                    } else if (message.getText().equals("Отмена ↪\uFE0F")) {
                        stepUser.removeStep(message.getChatId());
                        executeMessage(SendMessag.sendM(message.getChatId(), "Процесс отменен", ReplayMarkap.menuRu(message.getChatId().toString())));
                    } else if (!stepUser.getStatus(message.getChatId()) && stepUser.getStep(message.getChatId()).equals(Step.BONUS_COD)) {
                        if (message.getText().length() <= 10 || isAlphaNumericOnly(message.getText())) {
                            CheckCod cod = requestService.checkCode(message.getText());
                            if (cod.getSuccess()) {
                                stepUser.setStep(message.getChatId(), Step.SEND_PHOTO);
                                bonusSave.setBonusMap(message.getChatId(), message);
                                if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                                    executeMessage(SendMessag.sendM(message.getChatId(), "Promo Code Muvaffaqiyatli  ✅ \n\n endi 3 tadan kam bo'lmagan photo yuboring !"));
                                } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                                    executeMessage(SendMessag.sendM(message.getChatId(), "Промокод успешен ✅\n" +
                                            "\n" +
                                            " Теперь пришлите минимум 3 фотографии!"));
                                }
                            } else {
                                if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                                    executeMessage(SendMessag.sendM(message.getChatId(), cod.getMessage_uz() + "\uD83D\uDEAB"));
                                } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                                    executeMessage(SendMessag.sendM(message.getChatId(), cod.getMessage_ru() + "\uD83D\uDEAB"));
                                }
                            }
                        } else {
                            if (usersMap.getUserDto(message.getChatId()).getLang().equals("uz")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Proma kod mavjud emas \uD83D\uDEAB"));
                            } else if (usersMap.getUserDto(message.getChatId()).getLang().equals("ru")) {
                                executeMessage(SendMessag.sendM(message.getChatId(), "Нет доступного промокода \uD83D\uDEAB"));
                            }
                        }
                    } else if (message.getText().equals("Связаться с нами \uD83D\uDCDE✉\uFE0F")) {
                        Info info = requestService.info();StringBuilder b=new StringBuilder();
                        for (Phone p:info.getPhones()){
                            b.append(p.getPhone()+"\n");
                        }
                        executeMessage(SendMessag.sendM(
                                message.getChatId(), "Link: " + info.getLink() + "\n" +
                                        "Phone: \n" + b
                        ));
                    } else if (message.getText().equals("Biz bilan bog'lanish \uD83D\uDCDE✉\uFE0F")) {
                        Info info = requestService.info();
                       StringBuilder b=new StringBuilder();
                        for (Phone p:info.getPhones()){
                            b.append(p.getPhone()+"\n");
                        }
                        executeMessage(SendMessag.sendM(
                                message.getChatId(), "Link: " + info.getLink() + "\n" +
                                        "Phone: \n" + b
                        ));
                    } else if (message.getText().equals("Мой аккаунт \uD83D\uDC64")) {
                        UsersDto usersDto = requestService.getUsers(message.getChatId(),requestUrl.getGetUserRU().toString());
                        executeMessage(SendMessag.sendM(message.getChatId(), String.format(TextRu.myInfo, usersDto.getSumma(),
                                usersDto.getName(),
                                usersDto.getPhone(),
                                usersDto.getRegion(),
                                usersDto.getLang().toUpperCase()), InlineButtons.editeLang(usersDto.getLang().toLowerCase())));
                    } else if (message.getText().equals("Mening Akkauntim \uD83D\uDC64")) {
                        UsersDto usersDto = requestService.getUsers(message.getChatId(),requestUrl.getGetUserUz().toString());
                        executeMessage(SendMessag.sendM(message.getChatId(), String.format(TextUz.myInfo, usersDto.getSumma(),
                                usersDto.getName(),
                                usersDto.getPhone(),
                                usersDto.getRegion(),
                                usersDto.getLang().toUpperCase()), InlineButtons.editeLang(usersDto.getLang().toLowerCase())));
                    }
                }else {
                    if (!message.getText().equals("/start")&&stepUser.getStatus(message.getChatId())) {
                        stepUser.setStep(message.getChatId(), Step.LANG);
                        executeMessage(SendMessag.sendM(message.getChatId(),RegisTextUz.reset + "\n\n" + RegisTextRu.reset,ReplayMarkap.removeKeyboard()));
                        executeMessage(SendMessag.sendM(message.getChatId(), TextUz.start + "\n" + TextRu.start, InlineButtons.lan()));
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
                bonusSave.setBonusMap(message.getChatId(),message);
                if (bonusSave.getBonusSize(message.getChatId()).size()==4){
                    List<Message> photoList= bonusSave.getBonusSize(message.getChatId());
                    for (int i = 1; i <photoList.size(); i++) {
                        ForwardMessage forwardMessage=new ForwardMessage();
                        forwardMessage.setMessageId(photoList.get(i).getMessageId());
                        forwardMessage.setFromChatId(photoList.get(i).getChatId());
                        forwardMessage.setChatId(groupId);
                        try {
                            execute(forwardMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    SendMessage sendPhoto=new SendMessage();
                    sendPhoto.setChatId(groupId);
                    sendPhoto.setParseMode(ParseMode.HTML);
                    sendPhoto.setReplyMarkup(InlineButtons.checkGroupPhoto(usersMap.getUserDto(message.getChatId()).getLang(),photoList.get(0).getChatId().toString()));
                    stepUser.removeStep(message.getChatId());
                    if (usersMap.getUserDto(photoList.get(0).getChatId()).getLang().equals("uz")) {
                        sendPhoto.setText(String.format(String.format(TextUz.checkGroupPhoto,bonusSave.getBonusSize(message.getChatId()).get(0).getText()),bonusSave.getBonusSize(message.getChatId()).get(0).getText()));
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
                            .text(String.format(RegisTextUz.infoUser, usersDto.getName(), usersDto.getPhone(), update.getCallbackQuery().getData().split("_")[1], usersDto.getLang().toUpperCase())+"\n\n\n"+RegisTextUz.menu)
                            .chatId(message.getChatId())
                            .messageId(message.getMessageId())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(InlineButtons.goodUz())
                            .build()
                    );
                } else if (usersDto.getLang().equals("ru")) {
                    executeMessage(EditMessageText.builder()
                            .text(String.format(RegisTextRu.infoUser, usersDto.getName(), usersDto.getPhone(), update.getCallbackQuery().getData().split("_")[1], usersDto.getLang().toUpperCase())+"\n\n\n"+RegisTextRu.menu)
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
                        "\nTasdiqladi: "+update.getCallbackQuery().getFrom().getFirstName()+" "+update.getCallbackQuery().getFrom().getLastName() +"" +
                        "\nMijoz: "+usersMap.getUserDto(chatId).getName()+"" +
                        "\nchatId: #"+chatId).messageId(message.getMessageId()).chatId(message.getChatId()).build());
                bonusSave.deleteBonus(chatId);


            } else if (update.getCallbackQuery().getData().startsWith("NOTSAVE_PHOTO_")) {
                Long chatId=Long.valueOf(update.getCallbackQuery().getData().split("_")[2]);
                if (usersMap.getUserDto(chatId).getLang().equals("uz")) {
                    stepUser.removeStep(chatId);
                    executeMessage(SendMessag.sendM(chatId, "Bonus xisoblanmadi ❌",ReplayMarkap.menuUz(chatId.toString())));
                } else if (usersMap.getUserDto(chatId).getLang().equals("ru")) {
                    stepUser.removeStep(chatId);
                    executeMessage(SendMessag.sendM(chatId, "Бонус не засчитывается ❌",ReplayMarkap.menuRu(chatId.toString())));
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
                UsersDto usersDto=null;
                if (update.getCallbackQuery().getData().equals("uz")) {
                    usersDto = requestService.getUsers(message.getChatId(),requestUrl.getGetUserUz().toString() );
                } else if (update.getCallbackQuery().getData().equals("ru")) {
                    usersDto = requestService.getUsers(message.getChatId(),requestUrl.getGetUserRU().toString() );
                }
                stepUser.removeStep(message.getChatId());
                requestService.patchUser(message.getChatId(), UserPatchLang.builder()
                                .lang(update.getCallbackQuery().getData())
                        .build());
                if (update.getCallbackQuery().getData().equals("uz")) {
                    executeMessage(EditMessageText.builder().text(String.format(TextUz.myInfo, usersDto.getSumma(),
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
                    executeMessage(EditMessageText.builder().text(String.format(TextRu.myInfo, usersDto.getSumma(),
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
        }else if (update.hasChannelPost()){
            if ( hashtag(update.getChannelPost().getText())!=0){
                    requestService.helpSave(Help.builder()
                                    .id(hashtag(update.getChannelPost().getText()))
                                    .file_id(null)
                                    .from_id(update.getChannelPost().getChatId().toString())
                                    .message_id(update.getChannelPost().getMessageId())
                                    .message_lang(hashtag(update.getChannelPost().getText())==1?"UZ":"RU")
                                    .message_text(update.getChannelPost().getText().replace("#"+hashtag(update.getChannelPost().getText()),""))
                                    .message_type(TypeMessage.TEXT.toString())
                            .build());
            } else if (hashtag(update.getChannelPost().getCaption())!=0) {
                if (update.getChannelPost().hasPhoto()) {
                    requestService.helpSave(Help.builder()
                            .id(hashtag(update.getChannelPost().getCaption()))
                            .file_id(update.getChannelPost().getPhoto().get(update.getChannelPost().getPhoto().size()-1).getFileId())
                            .from_id(update.getChannelPost().getChatId().toString())
                            .message_id(update.getChannelPost().getMessageId())
                            .message_lang(hashtag(update.getChannelPost().getCaption())==1?"UZ":"RU")
                            .message_text(update.getChannelPost().getCaption().replace("#"+hashtag(update.getChannelPost().getCaption()),""))
                            .message_type(TypeMessage.PHOTO.toString())
                            .build());
                } else if (update.getChannelPost().hasVideo()) {
                    requestService.helpSave(Help.builder()
                            .id(hashtag(update.getChannelPost().getCaption()))
                            .file_id(update.getChannelPost().getVideo().getFileId())
                            .from_id(update.getChannelPost().getChatId().toString())
                            .message_id(update.getChannelPost().getMessageId())
                            .message_lang(hashtag(update.getChannelPost().getCaption())==1?"UZ":"RU")
                            .message_text(update.getChannelPost().getCaption().replace("#"+hashtag(update.getChannelPost().getCaption()),""))
                            .message_type(TypeMessage.VIDEO.toString())
                            .build());
                } else if (update.getChannelPost().hasDocument()) {
                    requestService.helpSave(Help.builder()
                            .id(hashtag(update.getChannelPost().getCaption()))
                            .file_id(update.getChannelPost().getDocument().getFileId())
                            .from_id(update.getChannelPost().getChatId().toString())
                            .message_id(update.getChannelPost().getMessageId())
                            .message_lang(hashtag(update.getChannelPost().getCaption())==1?"UZ":"RU")
                            .message_text(update.getChannelPost().getCaption().replace("#"+hashtag(update.getChannelPost().getCaption()),""))
                            .message_type(TypeMessage.DOCUMENT.toString())
                            .build());
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
            } else if (o instanceof CopyMessage) {
                execute((CopyMessage) o);
            }else if (o instanceof SendVideo) {
                execute((SendVideo) o);
            }else if (o instanceof SendPhoto) {
                execute((SendPhoto) o);
            }else if (o instanceof SendDocument) {
                execute((SendDocument) o);
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
    public Integer hashtag(String text){
        if (text!=null) {
            Pattern pattern = Pattern.compile("#(\\d+)");
            Matcher matcher = pattern.matcher(text);
            Integer number = 0;
            while (matcher.find()) {
                number = Integer.valueOf(matcher.group(1));
            }
            return number;
        }
        return 0;
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
