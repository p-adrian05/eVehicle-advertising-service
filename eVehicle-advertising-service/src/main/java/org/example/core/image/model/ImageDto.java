package org.example.core.image.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class ImageDto {

    private final int id;

    private final String path;

    private final String name;

    private final Timestamp uploadedTime;

}
