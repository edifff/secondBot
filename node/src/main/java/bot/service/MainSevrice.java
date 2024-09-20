package bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainSevrice {
    void processTextMessage(Update update);

    void processDocMessage(Update update);

    void processPhotoMessage(Update update);
}
