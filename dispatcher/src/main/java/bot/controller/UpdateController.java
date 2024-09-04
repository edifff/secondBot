package bot.controller;

import bot.service.UpdateProducer;
import bot.units.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bot.model.RabbitQueue.*;


@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer){

        this.messageUtils=messageUtils;
        this.updateProducer=updateProducer;
    }

    public void registerBot(TelegramBot telegramBot){
        this.telegramBot=telegramBot;
    }

    public void processUpdate(Update update){
        if (update==null){
            log.error("Received update is null");
            return;
        }
        if(update.hasMessage()){
            distributeMessagesByType( update);
        }else{
            log.error("Гnsupported message type is received: "+update);
        }
    }

    private void distributeMessagesByType(Update update){
        var message= update.getMessage();
        if (message.hasText()){
            processTextMessage(update);
        }else if(message.hasDocument()){
            processDocMessage(update);
        }else if (message.hasPhoto()){
            processPhotoMessage(update);
        }else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setFileReceivedView(Update update) {
        var sendMessage=messageUtils.generatingSendMessegeWithText(update,
                "Файл был получен. Ведется его обработка");
        setView(sendMessage);
    }
    private void processTextMessage(Update update){
       updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void processDocMessage(Update update){
        updateProducer.produce(DOC_MESSAGE_UPDATE, update);
    }

    private void  processPhotoMessage(Update update){
        updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        setFileReceivedView(update);
    }



    private void setUnsupportedMessageTypeView(Update update){
        var sendMessage=messageUtils.generatingSendMessegeWithText(update,
                "Unsupported type of message");
        setView(sendMessage);
    }
}
