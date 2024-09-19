package bot.service;

import bot.entity.enams.AppDocument;
import bot.entity.enams.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);

}
