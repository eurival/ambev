package com.ambev.order_viewer.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {

    private final Key jwtSecretKey;
    private final long jwtExpirationInMs = 86400000; // 1 dia (em milissegundos)

    public TokenProvider() {
        // Gera a chave secreta (em produção, use uma chave segura e configurável)
        this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    /**
     * Cria o token JWT baseado na autenticação.
     *
     * @param authentication informações do usuário autenticado
     * @return o token JWT
     */
    public String createToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Define o nome do usuário como "subject"
                .setIssuedAt(new Date()) // Data de criação
                .setExpiration(new Date(new Date().getTime() + jwtExpirationInMs)) // Data de expiração
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512) // Assinatura com a chave secreta
                .compact();
    }

    /**
     * Valida o token JWT.
     *
     * @param token o token a ser validado
     * @return `true` se o token for válido, caso contrário `false`
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token inválido ou expirado
        }
    }

    /**
     * Extrai o nome do usuário (subject) do token JWT.
     *
     * @param token o token JWT
     * @return o nome do usuário
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}
