package org.example.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.controller.dto.message.PartnerNamesDto;
import org.example.exceptions.DeleteMessageException;
import org.example.exceptions.UnknownMessageException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UpdateMessageException;
import org.example.model.Message;
import org.example.model.MessagePartner;
import org.example.repository.dao.MessageDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageDao messageDao;

    @Override
    public void createMessage(Message message) throws UnknownUserException {
        messageDao.createMessage(message);
    }

    @Override
    public void deleteMessage(Message message) throws DeleteMessageException, UnknownMessageException, UnknownUserException {
        messageDao.deleteMessage(message);
    }

    @Override
    public void updateMessage(Message message) throws UnknownUserException, UnknownMessageException, UpdateMessageException {
        messageDao.updateMessage(message);
    }

    @Override
    public Page<Message> getMessagesByUsernames(String username1, String username2, Pageable pageable) throws UnknownUserException {
        return messageDao.getMessagesByUsernames(username1, username2,pageable);
    }

    /**
     *
     * @param username
     * @return conversation partner name - boolean pair, where boolean is
     * true when the conversation contains at least one new message
     */
    @Override
    public Page<MessagePartner> getConversationUsernames(String username, Pageable pageable){
        List<MessagePartner> partnerNames = messageDao.getConversationPartnersUsername(username);
        int fromIndex = partnerNames.size();
        int toIndex = 0;
        if(pageable.getOffset()<partnerNames.size()){
            fromIndex = (int) pageable.getOffset();
        }
        if(pageable.getOffset()+pageable.getPageSize()< partnerNames.size()){
            toIndex = (int) (pageable.getOffset()+ pageable.getPageSize());
        }else {
            toIndex = partnerNames.size();
        }
        Page<MessagePartner> pagedPartnerNames = new PageImpl<>(partnerNames.subList(fromIndex, toIndex), pageable, partnerNames.size());
        return pagedPartnerNames
                .map(partner-> {
                    partner.setThereNewMessage(messageDao.isThereNewMessage(username,partner.getPartnerUsername()));
                    return partner;
                });
    }




    @Override
    public int newMessagesCount(String username){
      return messageDao.newMessagesCountForUser(username);
    }

}
