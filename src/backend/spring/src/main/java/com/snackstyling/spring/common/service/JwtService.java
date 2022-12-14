package com.snackstyling.spring.common.service;

import com.snackstyling.spring.common.domain.Token;
import com.snackstyling.spring.common.dto.AcTokenResponse;
import com.snackstyling.spring.common.dto.TokenDto;
import com.snackstyling.spring.common.exception.*;
import com.snackstyling.spring.common.repository.TokenRepository;
import com.snackstyling.spring.login.domain.Login;
import com.snackstyling.spring.login.repository.LoginRepository;
import com.snackstyling.spring.member.domain.Member;
import com.snackstyling.spring.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.token.secret.access}")
    private String ac_secret_key;
    @Value("${jwt.token.secret.refresh}")
    private String re_secret_key;
    private Long accessExpired= Duration.ofMinutes(30).toMillis(); //30분
    private Long refreshExpired=Duration.ofDays(7).toMillis(); //1주
    private final TokenRepository tokenRepository;
    private final LoginRepository loginRepository;
    private final MemberRepository memberRepository;
    public String createJsonWebToken(Map<String, Object> headers,  Map<String, Object> payloads,Long expired, String secret_key){
        Date now = new Date();
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expired))
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }
    public TokenDto createToken(Member member) {
        /*jwt-header setting*/
        Map<String, Object> headers=new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg","HS256");
        /*jwt-header payload(pk,email)*/
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("Key", member.getId());
        payloads.put("Email",member.getLogin().getEmail());
        String refresh=createJsonWebToken(headers,payloads, refreshExpired, re_secret_key);
        String access=createJsonWebToken(headers,payloads, accessExpired,ac_secret_key);
        tokenRepository.save(new Token(member.getLogin().getEmail(),refresh));
        return new TokenDto(refresh,access);
    }
    public Long getMemberId(String token){
        return Long.parseLong(Jwts.parser().setSigningKey(ac_secret_key)
                .parseClaimsJws(token)
                .getBody()
                .get("Key").toString());
    }
    public void validateToken(String token){
        try {
            Jwts.parser().setSigningKey(ac_secret_key).parseClaimsJws(token);
        } catch (ExpiredJwtException e){
            throw new NotUpgradeException("토큰이 만료되었습니다.");
        } catch (Exception e){
            throw new UnauthorizedException("정상적으로 발급된 토큰이 아닙니다.");
        }
    }
    public AcTokenResponse refreshCompare(String token){
        String email=Jwts.parser().setSigningKey(re_secret_key)
                .parseClaimsJws(token)
                .getBody()
                .get("Email").toString();
        Token temp=tokenRepository.findById(email).orElse(null);
        if(temp.getRefreshToken()==null){
            throw new NotUpgradeException("토큰이 만료되었습니다.");
        }
        if(!temp.getRefreshToken().equals(token)){
            throw new UnauthorizedException("정상적으로 발급된 토큰이 아닙니다.");
        }
        Login user=loginRepository.findByEmail(email);
        Member member=memberRepository.findByLogin(user);
        Map<String, Object> headers=new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg","HS256");
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("Key", member.getId());
        payloads.put("Email",email);
        return new AcTokenResponse(createJsonWebToken(headers,payloads,accessExpired,ac_secret_key));
    }
}