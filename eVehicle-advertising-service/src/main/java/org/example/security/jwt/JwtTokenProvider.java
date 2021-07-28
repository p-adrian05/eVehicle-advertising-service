package org.example.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.example.security.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    /**
     * THIS IS NOT A SECURE PRACTICE! For simplicity, we are storing a static key here. Ideally, in a
     * microservices environment, this key would be kept on a config-server.
     */
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    @Value("${security.jwt.token.expire-length:3600000}")
    private final long validityInMilliseconds = 36000000; // 10h

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(Authentication authentication) {
        Claims claims = Jwts.claims()
            .setSubject(authentication.getName())
            .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
            .setIssuedAt(new Date())
            .setIssuer("Advertisement_Service")
            .setAudience("Advertisement_Portal");

        return Jwts.builder()
            .setClaims(claims)
            .claim("auth",
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .collect(
                        Collectors.toList()))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Jws<Claims> claims = getClaims(token);
        String username = claims.getBody().getSubject();
        List<GrantedAuthority> authorities =
            ((List<String>) claims.getBody().get("auth")).stream().map(SimpleGrantedAuthority::new)
                .collect(
                    Collectors.toList());
        return new UsernamePasswordAuthenticationToken(username, "", authorities);
    }

    private Jws<Claims> getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Expired or invalid JWT token", HttpStatus.FORBIDDEN);
        }
        return true;
    }

}
