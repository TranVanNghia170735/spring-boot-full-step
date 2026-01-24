package com.backend_fullstep.model;

import com.backend_fullstep.common.Gender;
import com.backend_fullstep.common.UserStatus;
import com.backend_fullstep.common.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="tbl_user")
public class UserEntity extends  AbstractEntity<Long> implements UserDetails , Serializable {

    @Column(name="first_name", length = 255)
    private String firstName;

    @Column(name="last_name", length = 255)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="gender")
    private Gender gender;

    @Column(name="date_of_birth")
    private LocalDate birthday;

    @Column(name ="email", length = 255)
    private String email;

    @Column(name="phone", length = 15)
    private String phone;

    @Column(name="username", unique = true,nullable = false, length = 255)
    private String userName;

    @Column(name="password", length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="type", length = 255)
    private UserType type;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="status", length = 255)
    private UserStatus status;

    @OneToMany(mappedBy = "user")
    private Set<UserHasGroup> groups = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserHasRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<AddressEntity> addresses = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return "";
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
