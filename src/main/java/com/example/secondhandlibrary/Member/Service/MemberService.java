package com.example.secondhandlibrary.Member.Service;


import com.example.secondhandlibrary.Member.DTO.KakaoUserInfoResponseDTO;
import com.example.secondhandlibrary.Member.Entity.MemberEntity;
import com.example.secondhandlibrary.Member.Repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public void ExistsMember(KakaoUserInfoResponseDTO userInfo){
        if (!memberRepository.existsByAuthID(userInfo.getId())){
            Long authId = userInfo.getId();
            String nickName = userInfo.getKakaoAccount().getProfile().getNickName();
            String profile_img = userInfo.getKakaoAccount().getProfile().getThumbnailImageUrl();
            MemberEntity member = MemberEntity.builder()
                    .authID(authId)
                    .nickName(nickName)
                    .profile_img(profile_img)
                    .build();
            memberRepository.save(member);
        }
    }

}
