package org.example.controller.dto.advertisement;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SavedAdDto {

    private int adId;

    private String title;
}
