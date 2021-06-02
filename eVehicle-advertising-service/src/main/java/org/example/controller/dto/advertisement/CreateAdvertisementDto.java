package org.example.controller.dto.advertisement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.core.advertising.persistence.ProductState;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAdvertisementDto{

    @NotNull(message = "creator: cannot be null")
    @NotEmpty(message = "creator: cannot be empty")
    private String creator;
    @NotNull(message = "category: cannot be null")
    @NotEmpty(message = "category: cannot be empty")
    private String category;
    @NotNull(message = "type: cannot be null")
    @NotEmpty(message = "type: cannot be empty")
    private String type;
    @NotNull(message = "brand: cannot be null")
    @NotEmpty(message = "brand: cannot be empty")
    private String brand;
    @NotNull(message = "title: cannot be null")
    @NotEmpty(message = "title: cannot be empty")
    private String title;
    @NotNull(message = "currency: cannot be null")
    @NotEmpty(message = "currency: cannot be empty")
    private String currency;

    private ProductState condition;
    @NotNull(message = "price: cannot be null")
    @Min(0)
    private double price;
}
