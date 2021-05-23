package org.example.controller.dto.rate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.repository.util.RateState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRateDto {

    private String description;
    @NotNull(message = "ratingUsername: cannot be null")
    @NotEmpty(message = "ratingUsername: cannot be empty")
    private String ratingUsername;
    @NotNull(message = "ratedUsername: cannot be null")
    @NotEmpty(message = "ratedUsername: cannot be empty")
    private String ratedUsername;
    @NotNull(message = "adId: cannot be null")
    @Min(value = 1,message = "adId: cannot be null cannot be 0 or negative")
    private int adId;
    private RateState rateState;
}
