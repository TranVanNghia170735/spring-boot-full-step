package com.backend_fullstep.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tbl_role")
public class Role extends AbstractEntity<Long> {

    @Column(name="name")
    private String name;

//    @Column(name="description")
//    private String description;

    @OneToMany(mappedBy = "role")
    private Set<RoleHasPermission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<Group> groups = new HashSet<>();

    @OneToMany(mappedBy = "role")
    private Set<UserHasRole> roles = new HashSet<>();
}
