package org.example.core.advertising.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.core.advertising.persistence.ProductState;

@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class CreateAdDto {

        private final String creator;

        private final String category;

        private final String type;

        private final ProductState condition;

        private final String brand;

        private final String title;

        private final double price;

        private final String currency;
}
