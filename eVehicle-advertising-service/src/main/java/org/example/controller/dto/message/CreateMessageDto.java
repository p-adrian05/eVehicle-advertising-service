package org.example.controller.dto.message;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CreateMessageDto {

    @NotNull(message = "content: cannot be null")
    @NotEmpty(message = "content: cannot be empty")
    private String content;
    @NotNull(message = "senderUserName: cannot be null")
    @NotEmpty(message = "senderUserName: cannot be empty")
    private String senderUserName;

    private String receiverUsername;
}
