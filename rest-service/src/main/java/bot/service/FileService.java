package bot.service;

import bot.entity.enams.AppDocument;
import bot.entity.enams.AppPhoto;
import bot.entity.enams.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);

}
