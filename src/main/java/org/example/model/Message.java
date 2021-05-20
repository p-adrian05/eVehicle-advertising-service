package org.example.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
import java.util.List;


@Data
@Builder
@EqualsAndHashCode
public class Message {

    private int id;

    private String content;

    private boolean unread;

    private String senderUserName;

    private List<String> receiverUsernames;

    private Timestamp sentTime;
}
