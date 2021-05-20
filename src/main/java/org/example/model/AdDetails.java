package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.repository.entity.AdvertisementEntity;
import org.example.repository.util.Drive;
import org.example.repository.util.ProductState;

import javax.persistence.*;


@Data
@Builder
public class AdDetails {

    private int adId;

    private String description;

    private int range;

    private int weight;

    private double accelaration;

    private int maxSpeed;

    private int year;

    private int batterySize;

    private int km;

    private int chargeSpeed;

    private int seatNumber;

    private int performance;

    private Drive drive;

    private String color;
}
