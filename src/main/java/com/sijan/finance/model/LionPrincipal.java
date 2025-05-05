package com.sijan.finance.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class LionPrincipal implements UserDetails {

    private Lion lion;

    //constructor injection
    public LionPrincipal(Lion lion) {
        this.lion = lion;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return lion.getPassword();
    }

    @Override
    public String getUsername() {
        return lion.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Lion getLion() {
        return lion;
    }

    public void setLion(Lion lion) {
        this.lion = lion;
    }
}
