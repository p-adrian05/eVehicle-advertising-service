package org.example.core.message.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class MessagePartnerDto {

    private String partnerUsername;

    private String sentTime;

    private boolean isThereNewMessage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessagePartnerDto that = (MessagePartnerDto) o;
        return Objects.equals(partnerUsername, that.partnerUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partnerUsername);
    }
}
