package org.example.core.message.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class UserMessageId implements Serializable {

    private static final long serialVersionUID = 1L;

    private int messageId;
    private int senderId;
    private int receiverId;

}
