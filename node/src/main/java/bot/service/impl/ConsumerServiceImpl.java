package bot.service.impl;

import bot.service.ConsumerService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import static bot.model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService {

    @Override
    @RabbitListener(queues=TEXT_MESSAGE_UPDATE)
    public void consumerTextMessageUpdate(Update update) {
        log.debug("NODE: Text message is received");
    }

    @Override
    @RabbitListener(queues=PHOTO_MESSAGE_UPDATE)

    public void consumerPhotoMessageUpdate(Update update) {
        log.debug("NODE: Photo message is received");
    }

    @Override
    @RabbitListener(queues=DOC_MESSAGE_UPDATE)

    public void consumerDocMessageUpdate(Update update) {
        log.debug("NODE: Doc message is received");
    }
}
