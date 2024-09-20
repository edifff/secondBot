package bot.utils;

import org.hashids.Hashids;

public class CryptoTool {
    private final Hashids hashids;


    public CryptoTool(String salt) {
        var minLenght=10;
        this.hashids = new Hashids(salt,10);
    }

    public String hashOf(Long value){
        return hashids.encode(value);
    }

    public Long idOf(String value){
        long [] res= hashids.decode(value);
        if (value != null && res.length>0) {
            return res[0];
        }
        return null;
    }
}
