package org.example.repository.entity;

import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;

import javax.persistence.*;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "Roles")
@NaturalIdCache
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Role_Name")
    @NaturalId
    private String roleName;

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<UserEntity> users;
}
