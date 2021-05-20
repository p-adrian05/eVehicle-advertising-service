package org.example.repository.entity;

import lombok.*;
import org.example.repository.util.UserMessageId;

import javax.persistence.*;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "users_messages")
public class UserMessageEntity {

    @EmbeddedId
    private UserMessageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("messageId")
    @JoinColumn(name = "message_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private MessageEntity message;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("senderId")
    @JoinColumn(name = "sender_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity senderUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receiverId")
    @JoinColumn(name = "receiver_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity receiverUser;

    @Column
    private Timestamp sentTime;

    @Column
    private boolean unread;
}
