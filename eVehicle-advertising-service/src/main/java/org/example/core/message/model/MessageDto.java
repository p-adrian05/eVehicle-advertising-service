package org.example.core.message.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class MessageDto {

    private final int id;

    private final String content;

    private final boolean unread;

    private final String senderUserName;

    private final String receiverUsername;

    private final Timestamp sentTime;
}
