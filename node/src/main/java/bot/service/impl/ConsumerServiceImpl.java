package bot.service.impl;

import bot.service.ConsumerService;
import bot.service.MainSevrice;
import bot.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bot.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {
    private final MainSevrice mainSevrice;

    public ConsumerServiceImpl(MainSevrice mainSevrice) {
        this.mainSevrice = mainSevrice;
    }


    @Override
    @RabbitListener(queues=TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
        mainSevrice.processTextMessage(update);

    }

    @Override
    @RabbitListener(queues=PHOTO_MESSAGE_UPDATE)

    public void consumerPhotoMessageUpdate(Update update) {
        log.debug("NODE: Photo message is received");
        mainSevrice.processPhotoMessage(update);
    }

    @Override
    @RabbitListener(queues=DOC_MESSAGE_UPDATE)

    public void consumerDocMessageUpdate(Update update) {
        log.debug("NODE: Doc message is received");
        mainSevrice.processDocMessage(update);
    }
}
