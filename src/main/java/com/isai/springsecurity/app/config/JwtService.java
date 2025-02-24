package com.isai.springsecurity.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "c7VTGPn8VwT1riPUAu97bUudPLR5xZ4tb4nSHkYRq1pgpCL4SmPWCyexLREMRJLI";


    public String getUserName(String token) {
        return getClain(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return true;
    }

    public <T> T getClain(String token,
                          Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .
    }

    private String getSignInKey() {
        return null;
    }

}
