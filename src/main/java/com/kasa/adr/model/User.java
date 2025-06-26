package com.kasa.adr.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;

@Data
@Builder
@Document
public class User implements UserDetails {

    @Id
    String id;
    @Indexed(unique = true)
    String email;
    String password;
    String name;
    @Indexed(unique = true)
    String mobile;


    boolean emailVerified;
    boolean mobileVerified;
    boolean status;
    boolean passwordChangeRequired;

    String apiKey;
    UserType userType;

    Role role;

    // @DBRef
    ArbitratorProfile arbitratorProfile;

    //  @DBRef
    ClaimantProfile institutionProfile;

    Instant createdAt;

    String claimantAdminUserId;

    String profileImageUrl;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
}