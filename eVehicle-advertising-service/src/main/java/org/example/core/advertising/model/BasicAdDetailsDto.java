package org.example.core.advertising.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.core.advertising.persistence.Drive;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class BasicAdDetailsDto {

    private final int adId;

    private final int year;

    private final int batterySize;

    private final int km;

    private final int chargeSpeed;

    private final int seatNumber;

    private final int performance;

    private final Drive drive;
}
