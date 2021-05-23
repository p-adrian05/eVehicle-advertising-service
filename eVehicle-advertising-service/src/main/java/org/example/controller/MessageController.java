package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.AttributeNames;
import org.example.config.Mappings;
import org.example.controller.dto.message.CreateMessageDto;
import org.example.controller.dto.message.DeleteMessageDto;
import org.example.controller.dto.message.MessageDto;
import org.example.controller.dto.message.PartnerNamesDto;
import org.example.controller.dto.message.UpdateMessageDto;
import org.example.controller.util.ModelDtoConverter;

import org.example.core.message.MessageService;
import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.security.AuthException;
import org.example.core.user.exception.UnknownUserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
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

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping(Mappings.MESSAGES_PARTNERS)
    @CrossOrigin
    public Page<PartnerNamesDto> getMessagePartners(@RequestParam("username") String username,
                                                    @RequestParam(name = AttributeNames.PAGE_NUMBER, required = false, defaultValue = "0")
                                                        Integer page,
                                                    @RequestParam(name = AttributeNames.PAGE_SIZE, required = false, defaultValue = AttributeNames.MESSAGE_PARTNERS_PAGE_SIZE)
                                                        Integer size) {
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            throw new AuthException("Access Denied");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<MessagePartner> messagePartners = messageService.getConversationUsernames(username, pageable);
        List<PartnerNamesDto> partnerNamesDtos = messagePartners.stream().map(partner -> PartnerNamesDto.builder()
            .isThereNewMessage(partner.isThereNewMessage())
            .partnerUsername(partner.getPartnerUsername())
            .lastSentTime(partner.getSentTime()).build()).collect(Collectors.toList());
        return new PageImpl<>(partnerNamesDtos, pageable, messagePartners.getTotalPages());
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
        return messageService.getMessagesByUsernames(username, partnerUsername, pageable)
            .map(ModelDtoConverter::convertMessageDtoFromModel);
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
    public void createMessage(@Valid @RequestBody CreateMessageDto createMessageDto, BindingResult bindingResult)
        throws ValidationException, UnknownUserException, CreateMessageException {
        if (!SecurityContextHolder.getContext().getAuthentication().getName()
            .equals(createMessageDto.getSenderUserName())) {
            throw new AuthException("Access Denied");
        }
        if (createMessageDto.getSenderUserName().equals(createMessageDto.getReceiverUsername())) {
            throw new CreateMessageException("Sender and receiver user can not be the same");
        }
        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for creating user message", errors);
        }
        messageService.createMessage(Message.builder()
            .content(createMessageDto.getContent())
            .receiverUsernames(createMessageDto.getReceiverUsername())
            .senderUserName(createMessageDto.getSenderUserName())
            .build());
    }

    @PatchMapping(Mappings.MESSAGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CrossOrigin
    public void updateMessage(@Valid @RequestBody UpdateMessageDto updateMessageDto, BindingResult bindingResult)
        throws ValidationException, UnknownUserException, UnknownMessageException, UpdateMessageException {

        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for updating user message", errors);
        }
        messageService.updateMessage(Message.builder()
            .content(updateMessageDto.getContent())
            .id(updateMessageDto.getId())
            .unread(updateMessageDto.isUnread())
            .senderUserName(updateMessageDto.getSenderUsername())
            .receiverUsernames(List.of(updateMessageDto.getReceiverUsername()))
            .build());
    }

    @DeleteMapping(Mappings.MESSAGES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@Valid @RequestBody DeleteMessageDto deleteMessageDto, BindingResult bindingResult)
        throws ValidationException,
        DeleteMessageException, UnknownMessageException, UnknownUserException {
        if (bindingResult.hasErrors()) {
            List<String> errors = ModelDtoConverter.convertBindingErrorsToString(bindingResult.getAllErrors());
            throw new ValidationException("Validation failed for deleting user message", errors);
        }
        messageService.deleteMessage(Message.builder()
            .id(deleteMessageDto.getId())
            .senderUserName(deleteMessageDto.getSenderUserName())
            .receiverUsernames(List.of(deleteMessageDto.getReceiverUsername())).build());
    }

}
