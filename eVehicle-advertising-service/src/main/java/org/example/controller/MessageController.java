package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.message.CreateMessageDto;
import org.example.controller.dto.message.DeleteMessageDto;
import org.example.controller.dto.message.UpdateMessageDto;
import org.example.core.message.MessageService;
import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.message.model.MessageDto;
import org.example.core.message.model.MessagePartnerDto;
import org.example.security.exception.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping(Mappings.MESSAGES_PARTNERS)
    @CrossOrigin
    public Page<MessagePartnerDto> getMessagePartners(@RequestParam("username") String username,
                                                      @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false, defaultValue = "0")
                                                          Integer page,
                                                      @RequestParam(name = AttributeNames.PAGE_SIZE, required = false, defaultValue = AttributeNames.MESSAGE_PARTNERS_PAGE_SIZE)
                                                          Integer size) {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest.of(page, size);
        return messageService.getConversationUsernames(username, pageable);
    }

    @GetMapping(Mappings.MESSAGES)
    @CrossOrigin
    public Page<MessageDto> getMessages(@RequestParam("username") String username,
                                        @RequestParam("partnerUsername") String partnerUsername,
                                        @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false, defaultValue = "0")
                                            Integer page,
                                        @RequestParam(name = AttributeNames.PAGE_SIZE, required = false, defaultValue = AttributeNames.MESSAGES_PAGE_SIZE)
                                            Integer size,
                                        @RequestParam(name = AttributeNames.SORT_ORDER_BY_TIME, required = false, defaultValue = AttributeNames.MESSAGE_SORT_DEFAULT)
                                            String sortOrder) throws UnknownUserException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest
            .of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), AttributeNames.MESSAGE_TIME_ATTRIBUTE));
        return messageService.getMessagesByUsernames(username, partnerUsername, pageable);
    }

    @GetMapping(Mappings.MESSAGES_NEW_COUNT)
    public Integer getNewMessageNumber(@RequestParam("username") String username) {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            throw new AuthException("Access Denied");
        }
        return messageService.newMessagesCount(username);
    }

    @PostMapping(Mappings.MESSAGE)
    @ResponseStatus(HttpStatus.CREATED)
    @CrossOrigin
    public void createMessage(@Valid @RequestBody CreateMessageDto createMessageDto)
        throws UnknownUserException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName()
            .equals(createMessageDto.getSenderUserName())) {
            throw new AuthException("Access Denied");
        }
        if (createMessageDto.getSenderUserName().equals(createMessageDto.getReceiverUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sender and receiver user can not be the same");
        }
        messageService.createMessage(MessageDto.builder()
            .content(createMessageDto.getContent())
            .receiverUsername(createMessageDto.getReceiverUsername())
            .senderUserName(createMessageDto.getSenderUserName())
            .build());
    }

    @PatchMapping(Mappings.MESSAGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void updateMessage(@Valid @RequestBody UpdateMessageDto updateMessageDto)
        throws UpdateMessageException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName()
            .equals(updateMessageDto.getSenderUsername())) {
            throw new AuthException("Access Denied");
        }
        messageService.updateMessage(updateMessageDto.getId(), updateMessageDto.getContent());
    }

    @PatchMapping(Mappings.MESSAGES + "/"+Mappings.READ)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void readMessage(@Valid @RequestBody DeleteMessageDto readMessageDto)
        throws UnknownMessageException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName()
            .equals(readMessageDto.getReceiverUsername())) {
            throw new AuthException("Access Denied");
        }
        log.error(readMessageDto.toString());
        messageService.readMessage(readMessageDto.getId(), readMessageDto.getReceiverUsername(),
            readMessageDto.getSenderUsername());
    }

    @DeleteMapping(Mappings.MESSAGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@Valid @RequestBody DeleteMessageDto deleteMessageDto)
        throws DeleteMessageException, UnknownMessageException, UnknownUserException {
        messageService.deleteMessage(MessageDto.builder()
            .id(deleteMessageDto.getId())
            .senderUserName(deleteMessageDto.getSenderUsername())
            .receiverUsername(deleteMessageDto.getReceiverUsername()).build());
    }

}
