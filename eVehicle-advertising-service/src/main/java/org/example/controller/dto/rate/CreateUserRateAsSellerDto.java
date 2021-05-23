package org.example.controller.dto.rate;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.repository.util.RateState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRateAsSellerDto extends CreateUserRateDto {

    @NotNull(message = "activation code: cannot be null")
    private String activationCode;
}
