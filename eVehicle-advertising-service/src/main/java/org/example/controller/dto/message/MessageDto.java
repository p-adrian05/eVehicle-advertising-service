package org.example.controller.dto.message;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class MessageDto {

    private int id;

    private String content;

    private boolean unread;

    private String senderUserName;

    private String receiverUsername;

    private Timestamp sentTime;
}
