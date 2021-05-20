package org.example.repository.entity;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USERS")
@NaturalIdCache
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NaturalId
    private String username;

    @Column
    @ToString.Exclude
    private String password;

    @Column
    private String email;

    @Column
    private boolean enabled;

    @Column
    private String activation;

    @Column(name = "last_login")
    private Timestamp lastLogin;

    @Column
    private Timestamp created;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_img_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ImageEntity profileImage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Users_Roles",
            joinColumns = {@JoinColumn(name = "User_id")},
            inverseJoinColumns = {@JoinColumn(name = "Role_id")}
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<RoleEntity> roles = new HashSet<>();

    public void addRole(RoleEntity role){
        if(roles == null){
            this.roles = new HashSet<>();
        }
        if (roles.contains(role)){
            return;
        }
        roles.add(role);
    }

    public void removeRole(RoleEntity role){
        this.roles.remove(role);
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Saved_ads",
            joinColumns = {@JoinColumn(name = "User_id")},
            inverseJoinColumns = {@JoinColumn(name = "Ad_id")}
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<AdvertisementEntity> savedAds = new HashSet<>();

    public void addSavedAd(AdvertisementEntity entity){
        if (savedAds.contains(entity)){
            return;
        }
        savedAds.add(entity);
    }
    public void removeSavedAd(AdvertisementEntity entity){
        this.savedAds.remove(entity);
    }
}
