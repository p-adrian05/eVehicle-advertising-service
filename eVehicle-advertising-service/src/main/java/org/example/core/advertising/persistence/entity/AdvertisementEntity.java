package org.example.core.advertising.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.core.advertising.persistence.AdState;
import org.example.core.advertising.persistence.ProductState;
import org.example.core.image.persistence.entity.ImageEntity;
import org.example.core.user.persistence.entity.UserEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "AD")
public class AdvertisementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private TypeEntity type;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "savedAds")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> userEntities;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Ad_Images",
            joinColumns = {@JoinColumn(name = "Ad_id")},
            inverseJoinColumns = {@JoinColumn(name = "Image_id")}
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ImageEntity> images;

    @OneToOne(fetch = FetchType.LAZY,mappedBy = "advertisement")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BasicAdDetailsEntity basicAdDetails;

    @Column
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRODUCT_CONDITION")
    private ProductState productCondition;

    @Column
    private double price;

    @Column
    private String currency;

    @Column
    @Enumerated(EnumType.STRING)
    private AdState state;

    @Column
    private Timestamp created;

    public void addImage(ImageEntity imageEntity){
        this.getImages().add(imageEntity);
    }

    public void removeImage(ImageEntity imageEntity){
        this.getImages().remove(imageEntity);
    }
}
