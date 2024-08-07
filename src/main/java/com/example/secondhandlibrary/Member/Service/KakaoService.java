package com.example.secondhandlibrary.Member.Service;

import com.example.secondhandlibrary.Member.DTO.KakaoProfileResponseDTO;
import com.example.secondhandlibrary.Member.DTO.KakaoTokenResponseDTO;
import com.example.secondhandlibrary.Member.Entity.MemberEntity;
import com.example.secondhandlibrary.Member.Repository.MemberRepository;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.lang.reflect.Member;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private String clientId;
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private final MemberRepository memberRepository;

    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, MemberRepository memberRepository) {
        this.clientId = clientId;
        this.memberRepository = memberRepository;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
    }

    public String getAccessTokenFromKakao(String code) {

        KakaoTokenResponseDTO kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/oauth/token") //카카오 로그인 토근 받기
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .retrieve()
                //TODO : Custom Exception
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();


        log.info(" [Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(" [Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
        //제공 조건: OpenID Connect가 활성화 된 앱의 토큰 발급 요청인 경우 또는 scope에 openid를 포함한 추가 항목 동의 받기 요청을 거친 토큰 발급 요청인 경우
        log.info(" [Kakao Service] Id Token ------> {}", kakaoTokenResponseDto.getIdToken());
        log.info(" [Kakao Service] Scope ------> {}", kakaoTokenResponseDto.getScope());

        return kakaoTokenResponseDto.getAccessToken();
    }

    public MemberEntity getUserProfile(String accessToken) {
        KakaoProfileResponseDTO kakaoProfileResponseDto = WebClient.create(KAUTH_USER_URL_HOST)
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoProfileResponseDTO.class)
                .block();

        if (kakaoProfileResponseDto == null) {
            log.error("KakaoProfileResponseDTO is null");
            throw new RuntimeException("Failed to fetch user profile from Kakao");
        }

        KakaoProfileResponseDTO.KakaoAccount kakaoAccount = kakaoProfileResponseDto.getKakaoAccount();
        if (kakaoAccount == null) {
            log.error("KakaoAccount is null in KakaoProfileResponseDTO");
            throw new RuntimeException("Failed to fetch Kakao account information");
        }

        KakaoProfileResponseDTO.Properties properties = kakaoProfileResponseDto.getProperties();
        if (properties == null) {
            log.error("Properties is null in KakaoProfileResponseDTO");
            throw new RuntimeException("Failed to fetch properties information");
        }

        String email = kakaoAccount.getEmail();
        String nickname = properties.getNickname();
        String profileImage = properties.getProfileImage();

        log.info(" [Kakao Service] User Email ------> {}", email);
        log.info(" [Kakao Service] User Nickname ------> {}", nickname);
        log.info(" [Kakao Service] User Profile Image ------> {}", profileImage);

        // Member 객체 생성 및 디비 저장 로직
        MemberEntity member = MemberEntity.builder()
                .Email(email)
                .userName(nickname)
                .accessToken(accessToken)
                .build();
        memberRepository.save(member);

        return member;
    }


}
