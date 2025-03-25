package com.study.oauth.member.controller;

import com.study.oauth.common.auth.JwtTokenProvider;
import com.study.oauth.member.domain.Member;
import com.study.oauth.member.dto.MemberCreateDto;
import com.study.oauth.member.dto.MemberLoginDto;
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
}
