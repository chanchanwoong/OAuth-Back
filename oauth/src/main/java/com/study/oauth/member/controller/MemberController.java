package com.study.oauth.member.controller;

import com.study.oauth.common.auth.JwtTokenProvider;
import com.study.oauth.member.data.vo.RedirectVo;
import com.study.oauth.member.domain.Member;
import com.study.oauth.member.data.dto.MemberCreateDto;
import com.study.oauth.member.data.dto.MemberLoginDto;
import com.study.oauth.member.service.GoogleService;
import com.study.oauth.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleService googleService;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberCreateDto memberCreateDto) {
        Member member = memberService.create(memberCreateDto);
        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody MemberLoginDto memberLoginDto) {
        // email, password 일치 검증
        Member member = memberService.login(memberLoginDto);

        // 일치하면 jwt 생성
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());

        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    /**
     *  구글 인가코드를 받는 Controller
     *  - 프론트엔드에서 구글 인가코드를 가지고 유저 정보를 반환받는 Controller
     *  - 해당 Controller에서 HTTP Body를 통해 인가 코드를 받음
     *  - 해당 인가 코드를 가지고 구글 서버에 사용자 정보 요청
     *  - 사용자 정보를 통해 서비스 유저인지 확인
     *  - 유저인 경우, JWT 토큰 발급
     */
    @PostMapping("/google/doLogin")
    public ResponseEntity<?> googleLogin(@RequestBody RedirectVo redirectVo) {
        return new ResponseEntity<>(googleService.getToken(redirectVo), HttpStatus.OK);
    }
}
