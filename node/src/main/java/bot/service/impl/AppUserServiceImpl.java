package bot.service.impl;

import bot.dao.AppUserDao;
import bot.entity.enams.AppUser;
import bot.entity.enams.*;
import bot.service.AppUserService;
import bot.utils.CryptoTool;
import dto.MailParams;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import static bot.entity.enams.UserState.BASIC_STATE;
import static bot.entity.enams.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;
    @Value("${service.mail.uri}")
    private String mailServiceUri;

    public AppUserServiceImpl(AppUserDao appUserDao, CryptoTool cryptoTool) {
        this.appUserDao = appUserDao;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public String registerUser(AppUser appUser) {
        if(appUser.getIsActive()){
            return "Вы уже зарегестрированы";
        } else if (appUser.getEmail()!=null){
            return "Вам на почту отправлено письмо";
        }
        appUser.setState(WAIT_FOR_EMAIL_STATE);
        appUserDao.save(appUser);
        return "Введите, пожалуйста, свой email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            InternetAddress emailAddr=new InternetAddress(email);
            emailAddr.validate();
        }catch (AddressException e){
            return "Введите, пожалуйста, коректный email. Для отмены команды нажмите /cancel";
        }
        var optional=appUserDao.findByEmail(email);
        if(optional.isEmpty()){
            appUser.setEmail(email);
            appUser.setState(BASIC_STATE);
            appUser=appUserDao.save(appUser);

            var cryptoUserId= cryptoTool.hashOf(appUser.getId());
            var response=sendRequestToMailService(cryptoUserId, email);
            if(response.getStatusCode() != HttpStatus.OK){
                var msg=String.format("Отправка эл. письма на почту %s не удалось", email);
                log.error(msg);
                appUser.setEmail(null);
                appUserDao.save(appUser);
                return msg;
            }
            return "Вам на почту было отправлено письмо."
                    +"Перейдите по ссылке в письме для подтверждения регистрации.";
        } else{
            return "Этот email уже используется. Введите корректный email."
                    +"Для отмены команды нажмите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        var restTemplate= new RestTemplate();
        var headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams= MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();
        var request=new HttpEntity<MailParams>(mailParams,headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
