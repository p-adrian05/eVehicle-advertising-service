package org.example.controller.dto.advertisement;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.core.advertising.persistence.Drive;


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
