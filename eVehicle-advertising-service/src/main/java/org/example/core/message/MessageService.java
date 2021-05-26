package org.example.core.message;


import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.message.model.MessageDto;
import org.example.core.message.model.MessagePartnerDto;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    void createMessage(MessageDto messageDto) throws UnknownUserException;

    void deleteMessage(MessageDto messageDto)
        throws DeleteMessageException, UnknownMessageException, UnknownUserException;

    void updateMessage(int messageId, String content) throws UpdateMessageException;

    void readMessage(int messageId, String receiverUsername, String senderUsername) throws UnknownMessageException;

    Page<MessageDto> getMessagesByUsernames(String username1, String username2, Pageable pageable)
        throws UnknownUserException;

    Page<MessagePartnerDto> getConversationUsernames(String username, Pageable pageable);

    int newMessagesCount(String username);

    boolean isThereNewMessage(String receiverUsername, String senderUsername);
}
