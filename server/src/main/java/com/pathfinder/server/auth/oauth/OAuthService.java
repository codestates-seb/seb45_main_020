package com.pathfinder.server.auth.oauth;

import com.pathfinder.server.auth.jwt.dto.Token;
import com.pathfinder.server.auth.jwt.service.CustomUserDetails;
import com.pathfinder.server.auth.jwt.service.TokenProvider;
import com.pathfinder.server.auth.utils.AuthConstant;
import com.pathfinder.server.global.exception.authexception.OAuthCodeRequestException;
import com.pathfinder.server.global.exception.authexception.OAuthGithubRequestException;
import com.pathfinder.server.member.entity.Member;
import com.pathfinder.server.member.repository.MemberRepository;
import com.pathfinder.server.reward.entity.Reward;
import com.pathfinder.server.reward.repository.RewardRepository;
import com.pathfinder.server.reward.service.RewardService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
@Transactional(readOnly = true)
public class OAuthService {

    public static final String GITHUB_EMAIL_REQUEST_URL = "https://api.github.com/user/emails";
    private final InMemoryClientRegistrationRepository inMemoryRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final RestTemplate restTemplate;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final RewardService rewardService;
    private final RewardRepository rewardRepository;

    public OAuthService(InMemoryClientRegistrationRepository inMemoryRepository,
                        MemberRepository memberRepository,
                        TokenProvider tokenProvider,
                        RestTemplate restTemplate,
                        DefaultOAuth2UserService defaultOAuth2UserService,
                        RewardService rewardService,
                        RewardRepository rewardRepository) {
        this.inMemoryRepository = inMemoryRepository;
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
        this.restTemplate = restTemplate;
        this.defaultOAuth2UserService = defaultOAuth2UserService;
        this.rewardService = rewardService;
        this.rewardRepository = rewardRepository;
    }


    @Transactional
    public Token login(Provider provider, String code) {

        String registrationId = provider.getDescription(); // OAuth 서비스 이름(ex. kakao, naver, google)

        ClientRegistration clientRegistration = inMemoryRepository.findByRegistrationId(registrationId);

        String token = getToken(code, clientRegistration);

        OAuth2User oAuth2User = getOAuth2User(token, clientRegistration);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes()); // OAuth2UserService 를 통해 가져온 OAuth2User 의 attribut e를 담을 클래스

        if(registrationId.equals("github")){
            attributes.put("email", getGithubEmail(token));
        }

        MemberProfile memberProfile = Provider.extract(registrationId, attributes); // registrationId에 따라 유저 정보를 통해 공통된 UserProfile 객체로 만들어 줌

        Member member = getOrSaveMember(memberProfile);

        return createToken(member);
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_secret", provider.getClientSecret());
        formData.add("client_id",provider.getClientId());
        return formData;
    }

    private String getToken(String code, ClientRegistration clientRegistration) {

        String uri = clientRegistration.getProviderDetails().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_FORM_URLENCODED);
        headers.setAcceptCharset(List.of(UTF_8));

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(tokenRequest(code, clientRegistration), headers);

        try {
            ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            return responseEntity.getBody().get("access_token");

        } catch (HttpClientErrorException.BadRequest e) {
            throw new OAuthCodeRequestException();
        }
    }

    private OAuth2User getOAuth2User(String token, ClientRegistration clientRegistration) {

        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(token)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(3600L)
                .build();

        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());

        return defaultOAuth2UserService.loadUser(userRequest);
    }

    private String getGithubEmail(String accessToken){

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubEmail[]> response = restTemplate.exchange(
                GITHUB_EMAIL_REQUEST_URL,
                HttpMethod.GET,
                entity,
                GithubEmail[].class
        );

        if(response.getBody() != null && response.getBody().length > 0) {
            return response.getBody()[0].getEmail();
        }

        throw new OAuthGithubRequestException();
    }

    private Member getOrSaveMember(MemberProfile memberProfile) {
        Member member = getMember(memberProfile);
        if (member == null) {
            member = saveMember(memberProfile);
        }
        return member;
    }

    private Member getMember(MemberProfile memberProfile) {

        return memberRepository.findByEmail(memberProfile.getEmail())
                .orElse(null);
    }

    private Member saveMember(MemberProfile memberProfile) {
        Member member = Member.createMember(
                memberProfile.getEmail(),
                memberProfile.getEmail().split("@")[0],
                "oauthUser");

        List<Reward> initialRewards = rewardService.createInitRewards(); // 초기 보상 리스트 생성
        for (Reward reward : initialRewards) {
            reward.setMember(member); // 각 보상에 해당 멤버 연결
        }
        rewardRepository.saveAll(initialRewards);

        return memberRepository.save(member);
    }

    private Token createToken(Member member) {
        CustomUserDetails userDetails = new CustomUserDetails(
                member.getMemberId(),
                member.getName(),
                member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(member.getAuthority().toString())));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String accessToken = tokenProvider.generateAccessToken(authentication, AuthConstant.ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = tokenProvider.generateRefreshToken(authentication, AuthConstant.ACCESS_TOKEN_EXPIRE_TIME);

        return new Token(accessToken, refreshToken, member.getMemberId());
    }

    @Getter
    @Setter
    public static class GithubEmail {

        private String email;
        private boolean primary;
        private boolean verified;
        private String visibility;
    }
}
