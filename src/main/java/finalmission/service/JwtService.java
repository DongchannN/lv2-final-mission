package finalmission.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private Key key;

    public JwtService(@Value("${jwt.secret}") String jwtSecret) {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String payload) {
        return Jwts.builder()
                .claim("payload", payload)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24L))
                .setIssuedAt((new Date()))
                .signWith(key)
                .compact();
    }

    public String resolveToken(String token) {
        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return jws.getBody().get("payload", String.class);
        }
        catch (JwtException ex) {
            throw new IllegalArgumentException("");
        }
    }
}
