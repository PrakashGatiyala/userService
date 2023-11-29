package com.example.userservicenovttseve.security.model;

import com.example.userservicenovttseve.models.Role;
import com.example.userservicenovttseve.models.User;
import com.example.userservicenovttseve.security.service.CustomUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private User user;
    public CustomUserDetails(User user){
        this.user=user;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<CustomGrantedAuthority> customGrantedAuthorities= new ArrayList<>();
        for(Role role: user.getRoles()){
            customGrantedAuthorities.add(new CustomGrantedAuthority(role));
        }
        return customGrantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Keeping no account is expired
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // All account are never locked so true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Always set that password is not expired
        return true;
        // If lastUpdateDate is > 90 days return false, else true
    }

    @Override
    public boolean isEnabled() {
        // Enable all users
        return true;
    }
}
