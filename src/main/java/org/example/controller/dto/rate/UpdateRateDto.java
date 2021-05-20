package org.example.controller.dto.rate;

import lombok.Builder;
import lombok.Data;
import org.example.repository.util.RateState;
import org.example.repository.util.UserRateState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UpdateRateDto {

    private String description;

    @NotNull(message = "adId: cannot be null")
    @Min(value = 1,message = "adId: cannot be null cannot be 0 or negative")
    private int rateId;

    private RateState rateState;
}
