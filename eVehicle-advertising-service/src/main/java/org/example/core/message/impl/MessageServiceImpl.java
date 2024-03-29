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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final UserMessageRepository userMessageRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public void createMessage(MessageDto messageDto) throws UnknownUserException {
        Objects.requireNonNull(messageDto, "MessageDto cannot be null for crating message");
        Objects
            .requireNonNull(messageDto.getReceiverUsername(), "Receiver username cannot be null for creating message");
        Objects.requireNonNull(messageDto.getSenderUserName(), "Sender username cannot be null for creating message");
        Objects.requireNonNull(messageDto.getContent(), "Message content cannot be null for creating message");
        UserEntity senderUser = queryUserEntity(messageDto.getSenderUserName());
        log.info("Creating nem message, sender user: {}", senderUser);
        UserEntity receiverUserEntity = queryUserEntity(messageDto.getReceiverUsername());
        log.info("Creating nem message, receiver user: {}", receiverUserEntity);
        MessageEntity messageEntity = MessageEntity.builder()
            .content(messageDto.getContent())
            .build();
        MessageEntity newMessageEntity = messageRepository.save(messageEntity);
        log.info("New message: {}", messageEntity);
        UserMessageEntity userMessageEntity = UserMessageEntity.builder()
            .message(newMessageEntity)
            .id(new UserMessageId())
            .receiverUser(receiverUserEntity)
            .senderUser(senderUser)
            .unread(true)
            .sentTime(new Timestamp(new Date().getTime()))
            .build();
        userMessageRepository.save(userMessageEntity);
        log.info("Created new user-message connection: {}", userMessageEntity);
    }

    @Override
    public void deleteMessage(MessageDto messageDto)
        throws DeleteMessageException, UnknownMessageException {
        Objects.requireNonNull(messageDto, "MessageDto cannot be null for deleting message");
        Objects
            .requireNonNull(messageDto.getReceiverUsername(), "Receiver username cannot be null for deleting message");
        Objects.requireNonNull(messageDto.getSenderUserName(), "Sender username cannot be null for deleting message");
        UserMessageEntity userMessageEntity =
            queryUserMessage(messageDto.getId(), messageDto.getReceiverUsername(), messageDto.getSenderUserName());
        if (!userMessageEntity.isUnread()) {
            throw new DeleteMessageException("Message is already have read, cannot delete");
        }
        userMessageRepository.delete(userMessageEntity);
        log.info("Deleted user message: {}",userMessageEntity);
    }

    @Override
    public void updateMessage(int messageId, String content) throws UpdateMessageException {
        Objects.requireNonNull(content, "Message content cannot be null for updating message");
            if (userMessageRepository.countDistinctByUnreadIsFalseAndMessage_Id(messageId) != 0) {
                throw new UpdateMessageException("Message is already have read, cannot update");
            }
            Optional<MessageEntity> messageEntity = messageRepository.findById(messageId);
            if (messageEntity.isPresent()) {
                messageEntity.get().setContent(content);
                log.info("Updated message : {}", messageEntity);
                messageRepository.save(messageEntity.get());
            } else {
                throw new UpdateMessageException("Message not exists");
            }
    }

    public void readMessage(int messageId, String receiverUsername, String senderUsername)
        throws UnknownMessageException {
        Objects
            .requireNonNull(receiverUsername, "Receiver username cannot be null for reading message");
        Objects.requireNonNull(senderUsername, "Sender username cannot be null for reading message");
        UserMessageEntity userMessageEntity =
            queryUserMessage(messageId, receiverUsername, senderUsername);
        userMessageEntity.setUnread(false);
        userMessageRepository.save(userMessageEntity);
        log.info("Read user message : {}", userMessageEntity);
    }

    @Override
    public Page<MessageDto> getMessagesByUsernames(String username1, String username2, Pageable pageable)
        throws UnknownUserException {
        Objects
            .requireNonNull(username1, "Username cannot be null for getting messages");
        Objects.requireNonNull(username2, "Username cannot be null for getting messages");
        Objects.requireNonNull(pageable, "Pageable cannot be null for getting messages");
        UserEntity userEntity1 = queryUserEntity(username1);
        UserEntity userEntity2 = queryUserEntity(username2);
        return userMessageRepository.findBySenderAndReceiverIds(userEntity1.getId(), userEntity2.getId(), pageable)
            .map(messageEntity -> {
                if (messageEntity.getId().getReceiverId() == userEntity1.getId()) {
                    messageEntity.setReceiverUser(userEntity1);
                    messageEntity.setSenderUser(userEntity2);
                } else {
                    messageEntity.setReceiverUser(userEntity2);
                    messageEntity.setSenderUser(userEntity1);
                }
                return MessageDto.builder()
                    .id(messageEntity.getMessage().getId())
                    .content(messageEntity.getMessage().getContent())
                    .sentTime(messageEntity.getSentTime())
                    .senderUserName(messageEntity.getSenderUser().getUsername())
                    .receiverUsername(messageEntity.getReceiverUser().getUsername())
                    .unread(messageEntity.isUnread()).build();
            });
    }

    private Set<MessagePartnerDto> getMessagePartners(String username) {
        Objects
            .requireNonNull(username, "Username cannot be null for getting message partners");
        Set<MessagePartnerDto> partnerNamesAndSentTime = new LinkedHashSet<>();

        userMessageRepository.getConversationPartnerNames(username).forEach(arr -> {
            if (arr[0].equals(username)) {
                MessagePartnerDto messagePartner = new MessagePartnerDto(arr[1], arr[2], false);
                partnerNamesAndSentTime.add(messagePartner);
            } else {
                MessagePartnerDto messagePartner = new MessagePartnerDto(arr[0], arr[2], false);
                partnerNamesAndSentTime.add(messagePartner);
            }
        });
        return partnerNamesAndSentTime;
    }

    @Override
    public Page<MessagePartnerDto> getConversationUsernames(String username, Pageable pageable) {
        Objects
            .requireNonNull(username, "Username cannot be null ");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        List<MessagePartnerDto> partnerNames = new LinkedList<>(getMessagePartners(username));
        Pair<Integer, Integer> indexFromTo = calcIndexPair(pageable, partnerNames.size());
        Page<MessagePartnerDto> pagedPartnerNames =
            new PageImpl<>(partnerNames.subList(indexFromTo.getFirst(), indexFromTo.getSecond()), pageable,
                partnerNames.size());
        return pagedPartnerNames
            .map(partner -> MessagePartnerDto.builder()
                .isThereNewMessage(isThereNewMessage(username, partner.getPartnerUsername()))
                .sentTime(partner.getSentTime())
                .partnerUsername(partner.getPartnerUsername())
                .build());
    }

    private Pair<Integer, Integer> calcIndexPair(Pageable pageable, int totalSize) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        int fromIndex = totalSize;
        int toIndex = 0;
        if (pageable.getOffset() < totalSize) {
            fromIndex = (int) pageable.getOffset();
        }
        if (pageable.getOffset() + pageable.getPageSize() < totalSize) {
            toIndex = (int) (pageable.getOffset() + pageable.getPageSize());
        } else {
            toIndex = totalSize;
        }
        return Pair.of(fromIndex, toIndex);
    }

    @Override
    public boolean isThereNewMessage(String receiverUsername, String senderUsername) {
        Objects
            .requireNonNull(receiverUsername, "Receiver username cannot be null");
        Objects.requireNonNull(senderUsername, "Sender username cannot be null");
        return userMessageRepository
            .existsDistinctByReceiverUser_UsernameAndSenderUser_UsernameAndUnreadIsTrue(receiverUsername,
                senderUsername);
    }

    @Override
    public int newMessagesCount(String username) {
        Objects.requireNonNull(username, "Username cannot be null");
        return userMessageRepository.countByReceiverUserUsernameAndUnreadIsTrue(username);
    }

    private UserEntity queryUserEntity(String username) throws UnknownUserException {
        Objects.requireNonNull(username, "Username cannot be null");
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        if (userEntity.isEmpty()) {
            throw new UnknownUserException(String.format("User not found: %s", username));
        }
        return userEntity.get();
    }

    private UserMessageEntity queryUserMessage(int messageId, String receiverUsername, String senderUsername)
        throws UnknownMessageException {
        Objects
            .requireNonNull(receiverUsername, "Receiver username cannot be null");
        Objects.requireNonNull(senderUsername, "Sender username cannot be null");
        Optional<UserMessageEntity> userMessageEntity = userMessageRepository
            .findByMessage_IdAndReceiverUser_UsernameAndAndSenderUser_Username(messageId, receiverUsername,
                senderUsername);
        if (userMessageEntity.isEmpty()) {
            throw new UnknownMessageException("Message not found");
        }
        return userMessageEntity.get();
    }

}
