package sn.sonatel.dsi.ins.imoc.config;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import sn.sonatel.dsi.ins.imoc.domain.Jwt;
import sn.sonatel.dsi.ins.imoc.service.AppUserService;
import sn.sonatel.dsi.ins.imoc.service.JwtService;

import java.io.IOException;

@Service
public class JwtFIlter extends OncePerRequestFilter {
    @Autowired
    private AppUserService appUserService;

    @Autowired
    private JwtService jwtService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null ;
        String header = null ;
        String username = null;
        boolean isTokenExpired = false;
        Jwt test_jwt = null ;

        final String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer")) {
            token = authorization.substring(7);

            isTokenExpired =jwtService.isTokenExpired(token) ;
              username =jwtService.extractUsernage(token);
            test_jwt = this.jwtService.tokenByValue(token);

        }
        if( test_jwt != null
            &&
            !isTokenExpired
            && test_jwt.getAppUser().getEmail().equals(username)
            && SecurityContextHolder.getContext().getAuthentication() == null
        )
        {
            UserDetails userDetails = appUserService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }
        filterChain.doFilter(request, response);

    }
}
