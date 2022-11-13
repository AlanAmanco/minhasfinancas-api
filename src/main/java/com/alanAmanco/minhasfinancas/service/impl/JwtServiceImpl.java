package com.alanAmanco.minhasfinancas.service.impl;

import com.alanAmanco.minhasfinancas.model.entity.Usuario;
import com.alanAmanco.minhasfinancas.service.JwtService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiracao}")
    private String expiracao;
    @Value("${jwt.chave-assinatura}")
    private String chaveAssinatura;

    @Override
    public String gerarTken(Usuario usuario) {
        long exp = Long.valueOf(expiracao);
        LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(exp);
        Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault() ).toInstant();
        java.util.Date data = Date.from(instant);

        return Jwts.builder()
                .setExpiration(data)
                .setSubject(usuario.getEmail())
                .claim("id",usuario.getId())
                .claim("nome",usuario.getNome())
                .claim("horaExpiracao", dataHoraExpiracao.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .signWith( SignatureAlgorithm.HS512, chaveAssinatura)
                .compact();
    }

    @Override
    public Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(chaveAssinatura)
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenValido(String token) {

        try{
            Claims claims = obterClaims(token);
            java.util.Date dataEx = claims.getExpiration();
            LocalDateTime dataExpiracao = dataEx.toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();

            return !LocalDateTime.now().isAfter(dataExpiracao);

        }catch (ExpiredJwtException e){
            return false;
        }
    }

    @Override
    public String obterLoginUsuario(String token) {

        Claims claims = obterClaims(token);
        return claims.getSubject();
    }
}
