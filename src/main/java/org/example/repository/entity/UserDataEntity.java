package org.example.repository.entity;

import lombok.*;
import org.example.model.UserData;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "Userdata")
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
