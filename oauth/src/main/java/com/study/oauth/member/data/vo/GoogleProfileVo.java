package com.study.oauth.member.data.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GoogleProfileVo {

    private final String sub;       // sub은 open_id(=social_id)를 의미
    private final String email;
}
