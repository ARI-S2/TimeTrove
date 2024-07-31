package com.timetrove.Project.common.config.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private final Long userCode;

    public CustomAuthenticationToken(Long userCode, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userCode = userCode;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userCode;
    }
}

