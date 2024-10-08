package bot.service.impl;

import bot.dao.AppUserDao;
import bot.service.UserActivationService;
import bot.utils.CryptoTool;
import org.springframework.stereotype.Service;

@Service
public class UserActivationServiceImp implements UserActivationService {
    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;


    public UserActivationServiceImp(AppUserDao appUserDao, CryptoTool cryptoTool) {
        this.appUserDao = appUserDao;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        var userId=cryptoTool.idOf(cryptoUserId);
        var optional=appUserDao.findById(userId);
        if(optional.isPresent()){
            var user=optional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
