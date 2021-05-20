package org.example.controller.dto.advertisement;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.repository.entity.AdvertisementEntity;
import org.example.repository.util.Drive;
import org.example.repository.util.ProductState;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementBasicDetailsDto {

    private int year;

    private int batterySize;

    private int km;

    private int chargeSpeed;

    private int seatNumber;

    private int performance;

    private Drive drive;
}
