package org.example.controller.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerNamesDto {

    private String partnerUsername;

    private String lastSentTime;

    private boolean isThereNewMessage;
}
