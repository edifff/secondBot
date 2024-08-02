package bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
public interface ConsumerService {
    void consumerTextMessageUpdate(Update update);
    void consumerPhotoMessageUpdate(Update update);
    void consumerDocMessageUpdate(Update update);

}
