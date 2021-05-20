package org.example.controller.dto.advertisement;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.repository.util.AdState;
import org.example.repository.util.ProductState;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdvertisementDto extends CreateAdvertisementDto{

    private int id;
}
