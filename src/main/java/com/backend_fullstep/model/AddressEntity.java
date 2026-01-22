package com.backend_fullstep.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name="tbl_address")
public class AddressEntity extends  AbstractEntity<Long>{

    @Column(name="apartment_number")
    private String apartmentNumber;

    @Column(name="floor")
    private String floor;

    @Column(name="building")
    private String building;

    @Column(name="street_number")
    private String streetNumber;

    @Column(name="street")
    private String street;

    @Column(name="city")
    private String city;

    @Column(name="country")
    private String country;

    @Column(name="address_type")
    private Integer addressType;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;

}
