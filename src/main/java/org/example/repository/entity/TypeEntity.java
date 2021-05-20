package org.example.repository.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "Type")
public class TypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BrandEntity brandEntity;

    @Column
    private String name;

}
