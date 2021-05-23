package org.example.core.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.core.message.MessageService;

import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.message.model.MessageDto;
import org.example.core.message.model.MessagePartnerDto;
import org.example.core.message.persistence.entity.MessageEntity;
import org.example.core.message.persistence.entity.UserMessageEntity;
import org.example.core.message.persistence.entity.UserMessageId;
import org.example.core.message.persistence.repository.MessageRepository;
import org.example.core.message.persistence.repository.UserMessageRepository;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.persistence.entity.UserEntity;
import org.example.core.user.persistence.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserMessageRepository userMessageRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public void createMessage(MessageDto messageDto) throws UnknownUserException {
        UserEntity senderUser = queryUserEntity(messageDto.getSenderUserName());
        log.info("Creating nem message, sender user: {}",senderUser);
        List<UserEntity> receiverUserEntities = new LinkedList<>();
        UserEntity receiverUser;
        for(String receiverUsername : messageDto.getReceiverUsernames()){
            receiverUser = queryUserEntity(receiverUsername);
            receiverUserEntities.add(receiverUser);
        }
        log.info("Creating nem message, receiver users: {}",receiverUserEntities);
        MessageEntity messageEntity = MessageEntity.builder()
            .content(messageDto.getContent())
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
    public void deleteMessage(MessageDto messageDto) throws DeleteMessageException, UnknownMessageException, UnknownUserException {
        int senderUserId = queryUserEntity(messageDto.getSenderUserName()).getId();
        int receiverUserId = queryUserEntity(messageDto.getReceiverUsernames().get(0)).getId();

        log.info("Deleting message, sender user id: {}",senderUserId);
        log.info("Deleting message, receiver user id: {}",receiverUserId);
        UserMessageId userMessageId = new UserMessageId(messageDto.getId(),senderUserId,receiverUserId);
        UserMessageEntity userMessageEntity = queryUserMessage(userMessageId);
        if(!userMessageEntity.isUnread()){
            throw new DeleteMessageException("Message is already have read, cannot delete");
        }
        userMessageRepository.deleteById(userMessageId);
        log.info("Deleted user-message connection: {}",userMessageEntity);
        if(userMessageRepository.countUserMessageEntityByMessage_Id(messageDto.getId())==0){
            messageRepository.deleteById(messageDto.getId());
            log.info("Deleted message id: {}",messageDto.getId());
        }
    }

    @Override
    public void updateMessage(MessageDto messageDto) throws UnknownUserException, UnknownMessageException,
        UpdateMessageException {
        int senderUserId = queryUserEntity(messageDto.getSenderUserName()).getId();
        int receiverUserId = queryUserEntity(messageDto.getReceiverUsernames().get(0)).getId();
        UserMessageId userMessageId = new UserMessageId(messageDto.getId(),senderUserId,receiverUserId);
        UserMessageEntity userMessageEntity =queryUserMessage(userMessageId);
        userMessageEntity.setUnread(messageDto.isUnread());
        userMessageRepository.save(userMessageEntity);

        if(messageDto.getContent() != null){
            if(userMessageRepository.countDistinctByUnreadIsFalseAndMessage_Id(messageDto.getId())!=0){
                throw new UpdateMessageException("Message is already have read, cannot update");
            }
            MessageEntity messageEntity = MessageEntity.builder()
                .id(messageDto.getId())
                .content(messageDto.getContent())
                .build();
            log.info("Updated message : {}",messageEntity);
            messageRepository.save(messageEntity);
        }
    }

    @Override
    public Page<MessageDto> getMessagesByUsernames(String username1, String username2, Pageable pageable) throws UnknownUserException {
        int user1Id = queryUserEntity(username1).getId();
        int user2Id =  queryUserEntity(username2).getId();
        log.info("Query messages for user: {}",user1Id);
        log.info("Query messages for user: {}",user2Id);
        return userMessageRepository.findBySenderAndReceiverIds(user1Id,user2Id,pageable)
            .map(messageEntity ->{
                MessageDto message = MessageDto.builder()
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

    /**
     *
     * @param username
     * @return conversation partner name - boolean pair, where boolean is
     * true when the conversation contains at least one new message
     */
    @Override
    public Page<MessagePartnerDto> getConversationUsernames(String username, Pageable pageable){
        List<MessagePartnerDto> partnerNamesAndSentTime = new LinkedList<>();
        userMessageRepository.getConversationPartnerNames(username).forEach(arr->{
            if(arr[0].equals(username)){
                MessagePartnerDto messagePartner = new MessagePartnerDto(arr[1],arr[2],false);
                if(!partnerNamesAndSentTime.contains(messagePartner)){
                    partnerNamesAndSentTime.add(messagePartner);
                }
            }else{
                MessagePartnerDto messagePartner = new MessagePartnerDto(arr[0],arr[2],false);
                if(!partnerNamesAndSentTime.contains(messagePartner)){
                    partnerNamesAndSentTime.add(messagePartner);
                }
            }
        });


        List<MessagePartnerDto> partnerNames = partnerNamesAndSentTime;
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
        Page<MessagePartnerDto> pagedPartnerNames = new PageImpl<>(partnerNames.subList(fromIndex, toIndex), pageable, partnerNames.size());
        return pagedPartnerNames
                .map(partner-> {
                    partner.setThereNewMessage(isThereNewMessage(username,partner.getPartnerUsername()));
                    return partner;
                });
    }

    @Override
    public boolean isThereNewMessage(String receiverUsername,String senderUsername){
        return userMessageRepository
            .existsDistinctByReceiverUser_UsernameAndSenderUser_UsernameAndUnreadIsTrue(receiverUsername,senderUsername);
    }
    @Override
    public int newMessagesCount(String username){
        return userMessageRepository.countByReceiverUserUsernameAndUnreadIsTrue(username);
    }
    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        log.info("Queried user : {}", userEntity.get());
        return userEntity.get();
    }

    private UserMessageEntity queryUserMessage(UserMessageId userMessageId) throws UnknownMessageException {
        Optional<UserMessageEntity> userMessageEntity = userMessageRepository
            .findById(userMessageId);
        if(userMessageEntity.isEmpty()){
            throw new UnknownMessageException(String.format("Message not found %s", userMessageId));
        }
        log.info("Queried user message : {}",userMessageEntity.get());
        return userMessageEntity.get();
    }

}
