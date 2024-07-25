package ua.berlinets.file_manager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
public class User implements UserDetails {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "account_is_confirmed")
    private boolean accountIsConfirmed;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "user_salt")
    private byte[] userSalt;

    @Column(name = "hashed_username")
    private String hashedUsername;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "refresh_token_exp")
    private LocalDateTime refreshTokenExp;

    @ManyToOne
    private StoragePlan storagePlan;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_username"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;


    public void addRoles(List<Role> roles) {
        this.roles.addAll(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role roleEnum : roles) {
            authorities.add(new SimpleGrantedAuthority(roleEnum.getRoleName().name()));
        }
        return authorities;
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
