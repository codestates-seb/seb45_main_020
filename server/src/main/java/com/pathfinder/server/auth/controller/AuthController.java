package com.pathfinder.server.auth.controller;

import com.pathfinder.server.auth.jwt.dto.Token;
import com.pathfinder.server.auth.oauth.OAuthService;
import com.pathfinder.server.auth.oauth.Provider;
import com.pathfinder.server.member.dto.MemberDto;
import com.pathfinder.server.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.pathfinder.server.auth.utils.AuthConstant.BEARER;
import static com.pathfinder.server.utils.UriCreator.createUri;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final OAuthService oAuthService;
    private final MemberService memberService;

    public AuthController(OAuthService oAuthService, MemberService memberService) {
        this.oAuthService = oAuthService;
        this.memberService = memberService;
    }

    // 로그인한 회원의 memberId와 함께 발급한 토큰 정보를 헤더로 반환
    @GetMapping("/oauth")
    public ResponseEntity<String> login(Provider provider, String code) {
        Token token = oAuthService.login(provider, code);

        HttpHeaders tokenHeader = getHttpHeaders(token);

        String jsonResponse = "{\"memberId\":" + token.getMemberId() + "}";

        return ResponseEntity.ok().headers(tokenHeader).body(jsonResponse);
    }

    // 토큰을 받아서 AccessToken과 RefreshToken의 정보를 포함한 헤더를 HttpHeaders 객체로 반환
    private HttpHeaders getHttpHeaders(Token token) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("Authorization", List.of(BEARER + token.getAccessToken()));
        map.put("Refresh", List.of(BEARER + token.getRefreshToken()));
        HttpHeaders tokenHeader = new HttpHeaders(map);
        return tokenHeader;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid MemberDto.Post request) {

        Long memberId = memberService.signup(request); //회원가입 기능 사용

        URI uri = createUri("/members/" , memberId);

        return ResponseEntity.created(uri).build();
    }

//     비밀번호 찾기 구현
//    @PatchMapping("/password")
//    public ResponseEntity<Void> findPassword() {
//    }
//
//     이메일 전송 기능 구현
//    @PostMapping("/email")
//    public ResponseEntity<Void> sendEmail() {
//    }


}