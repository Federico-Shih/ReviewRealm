package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.responses.UserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000;

    private static final long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Context
    private UriInfo uriInfo;

    public String getUsernameFromToken(String token) {
        try {
            return URLDecoder.decode(getClaimFromToken(token, Claims::getSubject), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException();
        }
    }

    public boolean isTokenRefresh(String token) {
        return getClaimFromToken(token, c -> c.get("refresh", Boolean.class) != null);
    }

    public String generateAccessToken(User user) {
        final Claims claims = Jwts.claims();
        claims.setSubject(getUserEncodedUrl(user));
        claims.setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION));
        claims.put("username", user.getUsername());
        claims.put("role", user.getRoles().toString());
        claims.put("email", user.getEmail());
        claims.put("id", user.getId());

        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setClaims(claims)
                .setIssuer("paw-2023a-04")
                .setIssuedAt(new Date())
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        final Claims claims = Jwts.claims();
        claims.setSubject(getUserEncodedUrl(user));
        claims.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME));
        claims.put("refresh", true);
        return Jwts.builder()
                .serializeToJsonWith(new JacksonSerializer<>(objectMapper))
                .setClaims(claims)
                .setIssuedAt(new Date())
                .signWith(secretKey)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = parseClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    private String getUserEncodedUrl(User user) {
        try {
            return URLEncoder.encode(user.getUsername(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unsupported encoding");
        }
    }
}
