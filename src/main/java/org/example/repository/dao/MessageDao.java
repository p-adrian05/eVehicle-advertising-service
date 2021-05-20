package org.example.repository.dao;

import org.example.exceptions.DeleteMessageException;
import org.example.exceptions.UnknownMessageException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UpdateMessageException;
import org.example.model.Message;
import org.example.model.MessagePartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;


public interface MessageDao {

    void createMessage(Message message) throws UnknownUserException;

    void deleteMessage(Message message) throws DeleteMessageException, UnknownMessageException, UnknownUserException;

    void updateMessage(Message message) throws UnknownUserException, UnknownMessageException, UpdateMessageException;

    Page<Message> getMessagesByUsernames(String username1, String username2, Pageable pageable) throws UnknownUserException;

    List<MessagePartner>  getConversationPartnersUsername(String username);

    boolean isThereNewMessage(String receiverUsername,String senderUsername);

    Integer newMessagesCountForUser(String username);
}
