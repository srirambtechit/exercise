package net.cloud.imageprocessor.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import net.cloud.imageprocessor.exception.UnauthorizedException;
import net.cloud.imageprocessor.model.JsonWebToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Log4j2
@Component
public final class JwtHelper {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private JwtHelper() {
        // Disclaimer just for PoC only, highly risky to logging secrets
        // However, intention of printing secretKey at startup of the
        // application is to help client to sign the request with the
        // same key at https://jwt.io portal
        // "/login" endpoints generates jwt token
        log.info("generated key: {}", getSecretString());
    }

    private String getSecretString() {
        return Encoders.BASE64.encode(key.getEncoded());
    }

    public boolean verify(String jwtString) {
        String jwsString = extractJwsString(jwtString);

        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwsString);
            // deep validations like claims, subject, expiry time, etc.,
            // can be done with `jws` variable for PoC, ignoring other checks
            assert jws.getHeader() != null;
            return true;
        } catch (JwtException ex) {
            // we *cannot* use the JWT as may be tampered
            throw new UnauthorizedException(ex.getMessage());
        }
    }

    public JsonWebToken parseClaims(String jwtString) {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(extractJwsString(jwtString)).getBody();

        return JsonWebToken.builder()
                .subject(body.getSubject())
                .issueAt(body.getIssuedAt())
                .audience(body.getAudience())
                .name(String.valueOf(body.get("name")))
                .tenantId(String.valueOf(body.get("tid")))
                .clientId(String.valueOf(body.get("oid")))
                .appId(String.valueOf(body.get("azp")))
                .email(String.valueOf(body.get("email"))).build();
    }

    private String extractJwsString(String jwtString) {
        String bearer = "Bearer ";
        int index = bearer.length();
        return jwtString.substring(index);
    }

    public static String generate() {
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject("1234567890")
                .setIssuedAt(Date.from(Instant.now()))
                .setAudience("com.company.jobservice")
                .claim("name", "John Doe")
                .claim("tid", 1)
                .claim("oid", 1)
                .claim("azp", "1")
                .claim("email", "customer@mail.com")
                .signWith(key).compact();
    }

}

