package org.example.controller.dto.rate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
