package org.example.repository.entity;

import lombok.*;
import org.example.repository.util.ImageExtension;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Data
@Table(name = "IMAGES")
public class ImageEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column
    private String path;

    @Column(name = "uploaded_time")
    private Timestamp uploadedTime;
}
