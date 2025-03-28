package com.study.oauth.member.service;

import com.study.oauth.common.auth.JwtTokenProvider;
import com.study.oauth.member.data.vo.AccessTokenVo;
import com.study.oauth.member.data.vo.GoogleProfileVo;
import com.study.oauth.member.data.vo.RedirectVo;
import com.study.oauth.member.domain.Member;
import com.study.oauth.member.domain.SocialType;
import com.study.oauth.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     *  getToken(RedirectVo)
     *  - 인가 코드를 통해 JWT 토큰을 반환받는다.
     *  - 이 과정에는 인가 코드를 통해 Access Token을 발급 받고, Access Token을 통해 구글로부터 사용자 정보를 받는다.
     *  - 사용자 정보를 본 서비스에서 검증하여 JWT 토큰을 발급해준다.
     */
    public Map<String, Object> getToken(RedirectVo redirectVo) {
        Map<String, Object> googleLoginInfo = new HashMap<>();

        AccessTokenVo accessTokenVo = getAccessToken(redirectVo.getCode());
        GoogleProfileVo googleProfileVo = getGoogleProfile(accessTokenVo.getAccessToken());

        // socialId를 통해 회원 탐색(없으면 null 반환)
        Member originalMember = memberRepository.findBySocialId(googleProfileVo.getSub()).orElse(null);

        // 최초 로그인인 경우, 회원 정보 저장
        if(originalMember == null) {
            originalMember = memberService.createOauth(googleProfileVo, SocialType.GOOGLE);
        }

        // JWT 토큰 발급
        String jwtToken = jwtTokenProvider.createToken(originalMember.getEmail(), originalMember.getRole().toString());

        googleLoginInfo.put("id", originalMember.getId());
        googleLoginInfo.put("token", jwtToken);

        return googleLoginInfo;
    }


    // Access Token 획득 메서드
    private AccessTokenVo getAccessToken(String code) {

    }

    // 사용자 정보 획득 메서드
    private GoogleProfileVo getGoogleProfile(String accessToken) {
    }
}
