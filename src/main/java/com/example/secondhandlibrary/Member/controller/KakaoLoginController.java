package com.example.secondhandlibrary.Member.controller;

import com.example.secondhandlibrary.Member.DTO.KakaoUserInfoResponseDTO;
import com.example.secondhandlibrary.Member.Service.KakaoService;
import com.example.secondhandlibrary.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    @Autowired
    private KakaoService kakaoService;

    @Autowired
    private MemberService memberService;

    @GetMapping("/kakaoLogin")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        log.info("발급받은 토큰 : " + accessToken);

        KakaoUserInfoResponseDTO userInfo = kakaoService.getUserInfo(accessToken);

        memberService.ExistsMember(userInfo);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
