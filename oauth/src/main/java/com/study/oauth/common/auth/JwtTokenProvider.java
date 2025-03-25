package com.study.oauth.common.auth;


import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final int expiration;
    private Key encoderSecretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;

        // application.yml에서 가져온 인코딩된 secretKey를 디코딩한 후,
        this.encoderSecretKey = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey),
                SignatureAlgorithm.HS512.getJcaName());
    }
}
