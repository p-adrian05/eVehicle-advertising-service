package org.example.controller.dto.message;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UpdateMessageDto {

    @NotNull(message = "adId: cannot be null")
    @Min(value = 1,message = "adId: cannot be null cannot be 0 or negative")
    private int id;

    private String content;

    private String senderUsername;

}
