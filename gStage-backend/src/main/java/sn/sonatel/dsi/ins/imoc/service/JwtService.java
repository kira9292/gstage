package sn.sonatel.dsi.ins.imoc.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Jwt;
import sn.sonatel.dsi.ins.imoc.repository.JwtRepository;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class JwtService {
    public static final String BEARER = "bearer";
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private JwtRepository jwtRepository;
    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    public Map<String, String> generate(String username) {
        AppUser user = (AppUser) this.appUserService.loadUserByUsername(username);
        this.disableToken(user);
        final Map<String, String> jwtMap = this.generatejwt(user);
        final Jwt jwt = Jwt
            .builder()
            .valeur(jwtMap.get(BEARER) )
            .desactive(false)
            .expire(false)
            .appUser(user)
            .build() ;
        this.jwtRepository.save(jwt);
        return jwtMap;
    }
    @Transactional(readOnly = true)
    public Map<String, String> generatejwt(AppUser user) {
        final long currentTime = System.currentTimeMillis();
        final long expirationTime = currentTime + 30*60*1000000;

       final Map<String,Object> claims = Map.of(
           "id",user.getId(),
            "service",user.getService().getName(),
            "departement",user.getService().getDepartemen().getName(),
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
            .signWith(getKey(), SignatureAlgorithm.HS256)
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

    public Jwt tokenByValue(String token) {
        return  this.jwtRepository.findByValeur(token.getBytes()).orElseThrow(()->
            new RuntimeException("user inconue"));
    }

    public void deconnexion() {
       AppUser user = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal() ;
       String mail = user.getEmail();
        Jwt jwt =this.jwtRepository.findUserValidToken(mail).orElseThrow(
            ()->new RuntimeException("token invalide")
        );
        jwt.setDesactive(true);
        jwt.setExpire(true);
        this.jwtRepository.save(jwt);

    }
    public void disableToken(AppUser user){
        final List<Jwt> jwtList = this.jwtRepository.finduser(user.getEmail()).peek(
            jwt -> {
                jwt.setDesactive(true);
                jwt.setExpire(true);
            }
        ).collect(Collectors.toList());
        this.jwtRepository.saveAll(jwtList);
    }
    @Scheduled(cron = "0 */1 * * * *")
    public void removeUseLessjwt(){
        this.jwtRepository.deleteByDesactiveAndExpire(true,true);
    }
}
