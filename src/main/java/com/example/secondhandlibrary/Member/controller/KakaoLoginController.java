package com.example.secondhandlibrary.Member.controller;

import com.example.secondhandlibrary.Member.Service.KakaoService;
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

    @GetMapping("/kakaoLogin")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        System.out.println("발급받은 토큰 : " + accessToken);
        kakaoService.getUserProfile(accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
