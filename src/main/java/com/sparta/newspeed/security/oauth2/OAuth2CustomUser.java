package com.sparta.newspeed.security.oauth2;

import com.sparta.newspeed.domain.user.entity.User;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Builder
public class OAuth2CustomUser implements OAuth2User {
    private String registrationId;
    private User user;
    private Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole().getAuthority();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return this.registrationId;
    }

    public User getUser() {
        return this.user;
    }

    public String getEmail() {
        return this.user.getUserEmail();
    }
}
