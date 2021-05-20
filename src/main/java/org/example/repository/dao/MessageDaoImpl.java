package org.example.repository.dao;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exceptions.DeleteMessageException;
import org.example.exceptions.UnknownMessageException;
import org.example.exceptions.UnknownUserException;
import org.example.exceptions.UpdateMessageException;
import org.example.model.Message;
import org.example.model.MessagePartner;
import org.example.repository.MessageRepository;
import org.example.repository.UserMessageRepository;
import org.example.repository.entity.MessageEntity;
import org.example.repository.entity.UserEntity;
import org.example.repository.entity.UserMessageEntity;
import org.example.repository.util.UserMessageId;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MessageDaoImpl implements MessageDao{

    private final UserMessageRepository userMessageRepository;
    private final MessageRepository messageRepository;
    private final EntityQuery entityQuery;


    @Override
    @Transactional
    public void createMessage(@NonNull Message message) throws UnknownUserException {
        UserEntity senderUser = entityQuery.queryUserEntity(message.getSenderUserName());
        log.info("Creating nem message, sender user: {}",senderUser);
        List<UserEntity> receiverUserEntities = new LinkedList<>();
        UserEntity receiverUser;
        for(String receiverUsername : message.getReceiverUsernames()){
            receiverUser = entityQuery.queryUserEntity(receiverUsername);
            receiverUserEntities.add(receiverUser);
        }
        log.info("Creating nem message, receiver users: {}",receiverUserEntities);
        MessageEntity messageEntity = MessageEntity.builder()
                .content(message.getContent())
                .build();
        MessageEntity newMessageEntity = messageRepository.save(messageEntity);
        log.info("New message: {}",messageEntity);
        for(UserEntity receiverUserEntity : receiverUserEntities){
            UserMessageEntity userMessageEntity = UserMessageEntity.builder()
                    .message(newMessageEntity)
                    .id(new UserMessageId())
                    .receiverUser(receiverUserEntity)
                    .senderUser(senderUser)
                    .unread(true)
                    .sentTime(new Timestamp(new Date().getTime()))
                    .build();
            userMessageRepository.save(userMessageEntity);
            log.info("Created new user-message connection: {}",userMessageEntity);
        }
    }

    @Override
    @Transactional
    public void deleteMessage(@NonNull Message message) throws DeleteMessageException, UnknownMessageException, UnknownUserException {
        int senderUserId = entityQuery.queryUserId(message.getSenderUserName());
        int receiverUserId = entityQuery.queryUserId(message.getReceiverUsernames().get(0));

        log.info("Deleting message, sender user id: {}",senderUserId);
        log.info("Deleting message, receiver user id: {}",receiverUserId);
        UserMessageId userMessageId = new UserMessageId(message.getId(),senderUserId,receiverUserId);
        UserMessageEntity userMessageEntity = entityQuery.queryUserMessage(userMessageId);
        if(!userMessageEntity.isUnread()){
            throw new DeleteMessageException("Message is already have read, cannot delete");
        }
        userMessageRepository.deleteById(userMessageId);
        log.info("Deleted user-message connection: {}",userMessageEntity);
        if(userMessageRepository.countUserMessageEntityByMessage_Id(message.getId())==0){
            messageRepository.deleteById(message.getId());
            log.info("Deleted message id: {}",message.getId());
        }
    }

    @Override
    @Transactional
    public void updateMessage(@NonNull Message message) throws UnknownMessageException, UpdateMessageException, UnknownUserException {
        int senderUserId = entityQuery.queryUserId(message.getSenderUserName());
        int receiverUserId = entityQuery.queryUserId(message.getReceiverUsernames().get(0));
        UserMessageId userMessageId = new UserMessageId(message.getId(),senderUserId,receiverUserId);
        UserMessageEntity userMessageEntity = entityQuery.queryUserMessage(userMessageId);
        userMessageEntity.setUnread(message.isUnread());
        userMessageRepository.save(userMessageEntity);

        if(message.getContent() != null){
            if(userMessageRepository.countDistinctByUnreadIsFalseAndMessage_Id(message.getId())!=0){
                throw new UpdateMessageException("Message is already have read, cannot update");
            }
            MessageEntity messageEntity = MessageEntity.builder()
                    .id(message.getId())
                    .content(message.getContent())
                    .build();
            log.info("Updated message : {}",messageEntity);
            messageRepository.save(messageEntity);
        }
    }

    @Override
    public Page<Message> getMessagesByUsernames(String username1, String username2, Pageable pageable) throws UnknownUserException {
        int user1Id = entityQuery.queryUserId(username1);
        int user2Id = entityQuery.queryUserId(username2);
        log.info("Query messages for user: {}",user1Id);
        log.info("Query messages for user: {}",user2Id);
        return userMessageRepository.findBySenderAndReceiverIds(user1Id,user2Id,pageable)
                .map(messageEntity ->{
                   Message message = Message.builder()
                    .id(messageEntity.getMessage().getId())
                    .content(messageEntity.getMessage().getContent())
                    .sentTime(messageEntity.getSentTime())
                    .unread(messageEntity.isUnread()).build();
                    if(messageEntity.getId().getReceiverId() == user1Id){
                        message.setReceiverUsernames(List.of(username1));
                        message.setSenderUserName(username2);
                    }else{
                        message.setReceiverUsernames(List.of(username2));
                        message.setSenderUserName(username1);
                    }
                    return message;
                });
    }
    @Override
    public List<MessagePartner> getConversationPartnersUsername(String username){
        List<MessagePartner> partnerNamesAndSentTime = new LinkedList<>();
        userMessageRepository.getConversationPartnerNames(username).forEach(arr->{
            if(arr[0].equals(username)){
                MessagePartner messagePartner = new MessagePartner(arr[1],arr[2],false);
                if(!partnerNamesAndSentTime.contains(messagePartner)){
                    partnerNamesAndSentTime.add(messagePartner);
                }
            }else{
                MessagePartner messagePartner = new MessagePartner(arr[0],arr[2],false);
                if(!partnerNamesAndSentTime.contains(messagePartner)){
                    partnerNamesAndSentTime.add(messagePartner);
                }
            }
        });
        return partnerNamesAndSentTime;
    }
    @Override
    public boolean isThereNewMessage(String receiverUsername,String senderUsername){
        return userMessageRepository
                .existsDistinctByReceiverUser_UsernameAndSenderUser_UsernameAndUnreadIsTrue(receiverUsername,senderUsername);
    }
    @Override
    public Integer newMessagesCountForUser(String username) {
        return userMessageRepository.countByReceiverUserUsernameAndUnreadIsTrue(username);
    }

}
