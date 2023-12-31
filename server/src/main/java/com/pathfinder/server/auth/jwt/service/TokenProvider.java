package com.pathfinder.server.auth.jwt.service;

import com.pathfinder.server.global.exception.authexception.JwtExpiredAuthException;
import com.pathfinder.server.global.exception.authexception.JwtNotVaildAuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import static com.pathfinder.server.auth.utils.AuthConstant.CLAIM_AUTHORITY;
import static com.pathfinder.server.auth.utils.AuthConstant.CLAIM_ID;

@Component
@Slf4j
public class TokenProvider {


    private final Key key;
    private final CustomUserDetailsService userDetailsService;

    public TokenProvider(@Value("${jwt.secret-key}") String secretKey,
                         CustomUserDetailsService userDetailsService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.userDetailsService = userDetailsService;
    }

    public String generateAccessToken(Authentication authentication, long accessTokenExpireTime) {

        // 사용자 정보로부터 Id와 권한 가져오기
        Long id = getIdFrom(authentication);
        String authorities = getAuthorityFrom(authentication);

        // 토큰 만료기간 설정
        Date AccessTokenExpiresIn = getExpireTime(accessTokenExpireTime);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(CLAIM_AUTHORITY, authorities)
                .claim(CLAIM_ID, id)
                .setExpiration(AccessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication, long refreshTokenExpireTime){

        // 사용자 정보로부터 Id 가져오기
        Long id = getIdFrom(authentication);

        // 토큰 만료기간 설정
        Date refreshTokenExpiresIn = getExpireTime(refreshTokenExpireTime);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(CLAIM_ID, id)
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Long getIdFrom(Authentication authentication) {

        Long id = null;

        if(authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            id = customUserDetails.getMemberId();
        }

        if(authentication.getPrincipal() instanceof DefaultOAuth2User){
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            id = principal.getAttribute(CLAIM_ID);
        }
        return id;
    }

    private String getAuthorityFrom(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private Date getExpireTime(long tokenExpireTime) {
        Date date = new Date();
        long now = date.getTime();

        return new Date(now + tokenExpireTime);
    }

    public String generateAccessTokenFrom(String refreshToken, long accessTokenExpireTime) {
        Claims claims = getParseClaims(refreshToken);
        String username = claims.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        return generateAccessToken(authentication, accessTokenExpireTime);
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new JwtNotVaildAuthException();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredAuthException();
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.");
            throw new JwtNotVaildAuthException();
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰입니다.");
            throw new JwtNotVaildAuthException();
        }
    }

    public Claims getParseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredAuthException();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new JwtNotVaildAuthException();
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.");
            throw new JwtNotVaildAuthException();
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new JwtNotVaildAuthException();
        }
    }


}
