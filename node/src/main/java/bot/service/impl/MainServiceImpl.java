package bot.service.impl;

import bot.dao.AppUserDao;
import bot.dao.RawDataDao;
import bot.entity.AppUser;
import bot.entity.RawData;
import bot.entity.enams.AppDocument;
import bot.exceptions.UploadFileException;
import bot.service.FileService;
import bot.service.MainSevrice;
import bot.service.ProducerService;
import bot.service.emans.ServiceCommand;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static bot.entity.enams.UserState.BASIC_STATE;
import static bot.entity.enams.UserState.WAIT_FOR_EMAIL_STATE;
import static bot.service.emans.ServiceCommand.*;

@Service
@Log4j
public class MainServiceImpl implements MainSevrice {
    private final RawDataDao rawDataDao;
    private final ProducerService producerService;
    private final AppUserDao appUserDao;
    private final FileService fileService;

    public MainServiceImpl(RawDataDao rawDataDao, ProducerService producerService, AppUserDao appUserDao, FileService fileService) {
        this.rawDataDao = rawDataDao;
        this.producerService = producerService;
        this.appUserDao = appUserDao;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update){
        saveRawData(update);
        var appUser=findOrSaveAppUser(update);
        var userState=appUser.getState();
        var text=update.getMessage().getText();
        var output="";

        var serviceCommand = ServiceCommand.fromValue(text);
        if(CANCEL.equals(serviceCommand)){
            output=cancelProcess(appUser);
        } else if (BASIC_STATE.equals(serviceCommand)){
            output=processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(serviceCommand)) {
            //TODO after
        } else {
            log.error("unknown user state "+ userState);
            output="Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        var chatId=update.getMessage().getChatId();
        sendAnswer(output, chatId);


}

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser=findOrSaveAppUser(update);
        var chatId=update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        try {
            AppDocument doc=fileService.processDoc(update.getMessage());
            var answer= "Документ успешно загружен!"
                    + "Download link: http://test.ru/get-doc/777";
            sendAnswer(answer, chatId);
        }
        //TODO app to save doc
        catch (UploadFileException ex ){
            log.error(ex);
            String error="К сожалению, загрузка не удалась. Повторите попытку позже";
            sendAnswer(error, chatId);
        }

    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState= appUser.getState();
        if (!appUser.getIsActive()){
            var error="арегистрируйтесь и введите свою учетную запись для загрузки контента";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error="завершите текущую команду, чтобы продолжить";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser=findOrSaveAppUser(update);
        var chatId=update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)){
            return;
        }
        //TODO app to save photo
        var answer= "Photo was download. Download link: http://test.ru/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage= new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String text) {
        if(REGISTRATION.equals(text)){
            return "Временно недоступно.";
        } else if (HELP.equals(text)) {
            return help();
        } else if (START.equals(text)){
            return "Приветствую! Чтобы посмотреть список доступных конанд нажмите /help";
        } else {
            return "неизвестная команда";
        }
    }

    private String help() {
        return "Список доступных команд: \n" +
                "cancel- отмена выполнения текущей команды \n" +
                "registration";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDao.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update){
        User telegramUser= update.getMessage().getFrom();
        AppUser persistentAppUser =appUserDao.findAppUserByTelegramUserId(telegramUser.getId());
        if(persistentAppUser==null){
            AppUser transientAppUser=AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(true)
                    .state(BASIC_STATE)
                    .build();
        return appUserDao.save(transientAppUser);
        }
        return persistentAppUser;
    }

    private void saveRawData(Update update) {
        RawData rawData =RawData.builder()
                .event(update)
                .build();
        rawDataDao.save(rawData);

    }
}
