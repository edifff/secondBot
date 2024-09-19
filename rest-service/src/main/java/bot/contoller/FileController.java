package bot.contoller;

import bot.service.FileService;
import lombok.extern.log4j.Log4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RequestMapping("/file")
@RestController
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id){
        var doc =fileService.getDocument(id);
        if (doc==null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent=doc.getBinaryContent();
        var fileSystemRecourse=fileService.getFileSystemResource(binaryContent);
        if(fileSystemRecourse==null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getMineType()))
                .header("Content-disposition", "attachment; filename"+doc.getDocName())
                .body(fileSystemRecourse);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id){
        var photo =fileService.getPhoto(id);
        if (photo==null){
            return ResponseEntity.badRequest().build();
        }
        var binaryContent=photo.getBinaryContent();
        var fileSystemRecourse=fileService.getFileSystemResource(binaryContent);
        if(fileSystemRecourse==null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemRecourse);
    }
}
