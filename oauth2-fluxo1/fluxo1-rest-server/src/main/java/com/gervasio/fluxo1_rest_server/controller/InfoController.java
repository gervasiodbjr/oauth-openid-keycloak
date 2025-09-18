package com.gervasio.fluxo1_rest_server.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("info")
public class InfoController {

    @GetMapping("token")
    String token(@AuthenticationPrincipal Jwt jwt) {
        return describeToken(jwt);
    }

    private String describeToken(Jwt jwt) {
        StringBuilder srtb = new StringBuilder();
        if (jwt != null) {
            srtb.append("Token: ").append(jwt.getTokenValue()).append(System.lineSeparator());
            srtb.append("Token Into:").append(System.lineSeparator());
            for (Map.Entry<String, Object> elem : jwt.getClaims().entrySet()) {
                String key = elem.getKey();
                Object value = elem.getValue();
                srtb.append("   ").append(key).append(": ").append(value).append(System.lineSeparator());    
            }
        }
        return srtb.toString();
    }

}
