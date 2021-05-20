package org.example.repository.entity;

import lombok.*;
import org.example.repository.util.AdState;
import org.example.repository.util.ProductState;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "Ad")
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

    @Column
    private ProductState condition;

    @Column
    private int price;

    @Column
    private AdState state;

    @Column
    private Timestamp created;

    public void addImage(ImageEntity imageEntity){
        if(images == null){
            this.images = new HashSet<>();
        }
        if (images.contains(imageEntity)){
            return;
        }
        images.add(imageEntity);
    }

    public void removeImage(ImageEntity imageEntity){
        this.images.remove(imageEntity);
    }
}
