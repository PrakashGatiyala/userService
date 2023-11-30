package com.example.userservicenovttseve.security.model;

import com.example.userservicenovttseve.models.Role;
import com.example.userservicenovttseve.models.User;
import com.example.userservicenovttseve.security.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@JsonDeserialize
//@NoArgsConstructor
public class CustomUserDetails implements UserDetails {
   // private User user;
    private List<GrantedAuthority> authorities;
    private String password;
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;



    private long userId;
    public CustomUserDetails(){

    }
    public CustomUserDetails(User user){
        authorities= new ArrayList<>();
        for(Role role: user.getRoles()){
            authorities.add(new CustomGrantedAuthority(role));
        }
        this.username=user.getEmail();
        this.password=user.getPassword();
        this.accountNonExpired=true;
        this.accountNonLocked=true;
        this.credentialsNonExpired=true;
        this.enabled=true;
        this.userId = user.getId();

    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

      return this.authorities;
    }

    @Override
    public String getPassword() {

        return this.password;
    }

    @Override
    public String getUsername() {

        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Keeping no account is expired
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        // All account are never locked so true
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Always set that password is not expired
        return this.credentialsNonExpired;
        // If lastUpdateDate is > 90 days return false, else true
    }

    @Override
    public boolean isEnabled() {
        // Enable all users
        return this.enabled;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
