package bot.units;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generatingSendMessegeWithText(Update update, String text){
        var message =update.getMessage();
        var sendMassege=new SendMessage();
        sendMassege.setChatId(message.getChatId().toString());
        sendMassege.setText(text);
        return sendMassege;
    }
}
