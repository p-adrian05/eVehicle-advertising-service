package org.example.core.user.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "USERDATA")
public class UserDataEntity {

    @Id
    private int userId;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity userEntity;

    @Column
    private String city;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "public_email")
    private String publicEmail;
    @Column(name = "phone_number")
    private String phoneNumber;

}
