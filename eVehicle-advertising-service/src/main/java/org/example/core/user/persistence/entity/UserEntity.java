package org.example.core.user.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.core.role.persistence.entity.RoleEntity;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
    private Set<RoleEntity> roles;

    public void addRole(RoleEntity role){
      this.getRoles().add(role);
    }

    public void removeRole(RoleEntity role){
        this.getRoles().remove(role);
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
