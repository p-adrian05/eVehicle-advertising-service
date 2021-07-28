package org.example.config;


import org.example.controller.ValidationException;
import org.example.core.advertising.exception.CreateAdvertisementException;
import org.example.core.advertising.exception.MaximumSavedAdsReachedException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.image.exception.UnknownImageException;
import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.example.security.exception.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

      @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
      @ExceptionHandler(value = {
          EmailAlreadyExistsException.class,
          UsernameAlreadyExistsException.class,
          UserRateAlreadyExistsException.class,})
      public ErrorInfo handleAlreadyExists(HttpServletRequest req, Exception ex) {
            return ErrorInfo.builder()
                .timestamp(new Timestamp(new Date().getTime()))
                .error(HttpStatus.UNPROCESSABLE_ENTITY.name())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();
      }

      @ResponseStatus(HttpStatus.NOT_FOUND)
      @ExceptionHandler(value = {
          UnknownUserException.class,
          UnknownRoleException.class,
          UnknownAdvertisementException.class,
          UnknownCategoryException.class,
          UnknownImageException.class,
          UnknownUserRateException.class,
          UnknownMessageException.class})
      public ErrorInfo handleNotFound(HttpServletRequest req, Exception ex) {
            return ErrorInfo.builder()
                .timestamp(new Timestamp(new Date().getTime()))
                .error(HttpStatus.NOT_FOUND.name())
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();
      }

      @ResponseStatus(HttpStatus.BAD_REQUEST)
      @ExceptionHandler(value = {ValidationException.class})
      public ErrorInfo handleValidationException(HttpServletRequest req, ValidationException ex) {
            ValidationErrorInfo errorInfo = new ValidationErrorInfo(new Timestamp(new Date().getTime()),
                    HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.name(), ex.getMessage(), req.getRequestURI());
            errorInfo.setErrors(ex.getErrors());
            return errorInfo;
      }
      @ResponseStatus(HttpStatus.CONFLICT)
      @ExceptionHandler(value = {DeleteMessageException.class, UpdateMessageException.class, CreateAdvertisementException.class,
          MaximumSavedAdsReachedException.class,IllegalArgumentException.class, AuthException.class})
      public ErrorInfo handleDeleteUpdateMessageException(HttpServletRequest req, Exception ex) {
            return ErrorInfo.builder()
                .timestamp(new Timestamp(new Date().getTime()))
                .error(HttpStatus.UNPROCESSABLE_ENTITY.name())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .message(ex.getMessage())
                .path(req.getRequestURI())
                .build();
      }

}
