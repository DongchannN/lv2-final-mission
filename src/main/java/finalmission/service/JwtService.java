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

    public static final String PAYLOAD = "payload";
    public static final String ROLE = "role";

    private Key key;

    public JwtService(@Value("${jwt.secret}") String jwtSecret) {
        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String payload, String role) {

        return Jwts.builder()
                .claim(PAYLOAD, payload)
                .claim(ROLE, role)
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24L))
                .setIssuedAt((new Date()))
                .signWith(key)
                .compact();
    }

    public <T> T resolveToken(String token, String claimName, Class<T> requiredType) {
        Jws<Claims> jws;
        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            final String string = jws.getBody().toString();
            System.out.println("string = " + string);
            return jws.getBody().get(claimName, requiredType);
        }
        catch (JwtException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("");
        }
    }
}
