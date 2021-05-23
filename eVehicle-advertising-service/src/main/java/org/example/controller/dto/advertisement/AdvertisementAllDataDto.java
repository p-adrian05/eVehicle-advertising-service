package org.example.controller.dto.advertisement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class AdvertisementAllDataDto extends AdvertisementDto {

    private String creator;

    private String category;
}
