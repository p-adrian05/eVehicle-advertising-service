package org.example.model;

import lombok.*;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class MessagePartner {

    private String partnerUsername;

    private String sentTime;

    private boolean isThereNewMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagePartner that = (MessagePartner) o;
        return Objects.equals(partnerUsername, that.partnerUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partnerUsername);
    }
}
