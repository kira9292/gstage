package sn.sonatel.dsi.ins.imoc.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Role;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Autowired
    private AppUserService appUserService;

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    public Map<String, String> generate(String username) {
        AppUser user = (AppUser) this.appUserService.loadUserByUsername(username);
        final Map<String, String> jwtMap = this.generatejwt(user);

        return jwtMap;
    }
    public Map<String, String> generatejwt(AppUser user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30*60*1000;

       final Map<String,Object> claims = Map.of(
            "nom",user.getName(),
           "username",user.getUsername(),
           "prenom",user.getFirstName(),
           Claims.EXPIRATION,new Date(expirationTime),
           Claims.SUBJECT,user.getEmail(),
           "roles", user.getAuthorities()
        );


        final String bearer = Jwts.builder()
            .setIssuedAt(new Date(currentTime))
            .setExpiration(new Date(expirationTime))
            .setSubject(user.getEmail())
            .setClaims(claims)
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .compact();

        return Map.of("bearer",bearer);
    }
    private Key getKey() {
        final byte[] decoder = Decoders.BASE64.decode(jwtKey);
        return Keys.hmacShaKeyFor(decoder);
    }

    public String extractUsernage(String token) {
        return this.getClaim(token, Claims::getSubject) ;
    }

    public boolean isTokenExpired(String token) {
        Date expirationDate=this.getClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }


    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(this.getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
