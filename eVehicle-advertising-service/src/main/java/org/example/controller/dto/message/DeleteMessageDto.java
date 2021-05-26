package org.example.controller.dto.message;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class DeleteMessageDto {

    @NotNull(message = "adId: cannot be null")
    @Min(value = 1,message = "adId: cannot be null cannot be 0 or negative")
    private int id;

    @NotNull(message = "senderUserName: cannot be null")
    @NotEmpty(message = "senderUserName: cannot be empty")
    private String senderUsername;

    @NotNull(message = "receiverUsername: cannot be null")
    @NotEmpty(message = "receiverUsername: cannot be empty")
    private String receiverUsername;
}
