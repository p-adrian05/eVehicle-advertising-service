package org.example.repository.util;
import lombok.*;

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
