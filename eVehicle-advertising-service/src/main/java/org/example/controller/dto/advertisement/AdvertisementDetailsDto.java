package org.example.controller.dto.advertisement;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.core.advertising.persistence.Drive;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementDetailsDto {

    @Min(value = 1900,message = "year: must be between 1900 and 2022")
    @Max(value = 2022,message = "year: must be between 1900 and 2022")
    private int year;

    @Size(max = 1500,message = "description: must length is 1500 character")
    private String description;
    @Min(value = 0,message = "range: must be positive or 0")
    @Max(value = 9999,message = "range: must be max 9 999")
    private int range;
    @Min(value = 0,message = "batterySize: must be positive or 0")
    @Max(value = 9999,message = "batterySize: must be max 9 999")
    private int batterySize;
    @Min(value = 0,message = "weight: must be positive or 0")
    @Max(value = 99999,message = "weight: must be max 99 999")
    private int weight;
    @Min(value = 0,message = "km: must be positive or 0")
    @Max(value = 9999999,message = "year: must be max 9 999 999")
    private int km;
    @Min(value = 0,message = "chargeSpeed: must be positive or 0")
    @Max(value = 99999,message = "chargeSpeed: must be max 99 999")
    private int chargeSpeed;
    @Min(value = 0,message = "accelaration: must be positive or 0")
    @Max(value = 999,message = "accelaration: must be max 999")
    private double accelaration;
    @Min(value = 0,message = "performance: must be positive or 0")
    @Max(value = 9999,message = "performance: must be max 9999")
    private int performance;
    @Min(value = 0,message = "chargeSpeed: must be positive or 0")
    @Max(value = 9999,message = "chargeSpeed: must be max 9 999")
    private int maxSpeed;
    @Min(value = 0,message = "seatNumber: must be positive or 0")
    @Max(value = 999,message = "seatNumber: must be max 999")
    private int seatNumber;

    private String color;

    private Drive drive;
}
