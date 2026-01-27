package com.backend_fullstep.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="tbl_token")
public class Token extends AbstractEntity<Integer>{

    @Column(name="username", unique = true)
    private String userName; // save username or email

    @Column(name="access_token")
    private String accessToken;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="reset_token")
    private String resetToken;
}
