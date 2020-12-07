package com.example.demo.domain.member.member;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Transient;
import java.util.List;

@Getter
public abstract class MemberUserDetails implements UserDetails {
    @Transient
    private final List<GrantedAuthority> authorities;
    @Transient
    private final boolean accountNonExpired;
    @Transient
    private final boolean accountNonLocked;
    @Transient
    private final boolean credentialsNonExpired;
    @Transient
    private final boolean enabled;

    MemberUserDetails(){
        authorities = AuthorityUtils.createAuthorityList("SCOPE_openid");
        accountNonExpired = accountNonLocked = credentialsNonExpired = enabled = true;
    }
}
