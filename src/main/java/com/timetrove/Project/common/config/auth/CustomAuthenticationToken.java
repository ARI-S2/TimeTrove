package com.timetrove.Project.common.config.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private final Long userCode;
    private final String token;

    public CustomAuthenticationToken(Long userCode, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userCode = userCode;
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userCode;
    }
}

