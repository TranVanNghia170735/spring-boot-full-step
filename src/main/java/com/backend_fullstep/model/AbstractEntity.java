package com.backend_fullstep.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity <T extends Serializable> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    T id;

//    @CreatedBy
//    @Column(name="create_by")
//    T createBy;
//
//    @LastModifiedBy
//    @Column(name="updated_by")
//    T updatedBy;


//    @Column(name = "created_at")
//    @CreationTimestamp
//    private LocalDate createdAt;
//
//    @Column(name = "updated_at")
//    @UpdateTimestamp
//    private LocalDate updatedAt;

}
