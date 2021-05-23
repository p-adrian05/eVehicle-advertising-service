package org.example.core.message.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Builder
@RequiredArgsConstructor
@Getter
public class MessagePartnerDto {

    private final String partnerUsername;

    private final String sentTime;

    private final boolean isThereNewMessage;

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
