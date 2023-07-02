package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationService {
    private final SecurityProperties securityProperties;

    public boolean verifyKey(String token) {
        return securityProperties.getApiKey().equals(token);
    }
}
