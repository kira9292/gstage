package sn.sonatel.dsi.ins.imoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;

import java.util.Map;

@Service
public class JwtService {
    @Autowired
    private AppUserService appUserService;


    public Map<String, String> generate(String username) {
        AppUser user = (AppUser) this.appUserService.loadUserByUsername(username);
        return null;
    }
    public Map<String, String> generatejwt(AppUser user) {

        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30*60*1000;

        return null;
    }

}
