package org.example.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
@Builder
public class Image {

    private int id;

    private String path;

    private String name;

    private Timestamp uploadedTime;

}
