package org.example.config;


import org.example.controller.ValidationException;
import org.example.core.advertising.exception.CreateAdvertisementException;
import org.example.core.advertising.exception.UnknownAdvertisementException;
import org.example.core.advertising.exception.UnknownCategoryException;
import org.example.core.image.exception.UnknownImageException;
import org.example.core.message.exception.DeleteMessageException;
import org.example.core.message.exception.UnknownMessageException;
import org.example.core.message.exception.UpdateMessageException;
import org.example.core.rating.exception.UnknownUserRateException;
import org.example.core.rating.exception.UserRateAlreadyExistsException;
import org.example.core.role.exception.UnknownRoleException;
import org.example.core.security.AuthException;
import org.example.core.user.exception.EmailAlreadyExistsException;
import org.example.core.user.exception.UnknownUserException;
import org.example.core.user.exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.Date;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

      @ResponseStatus(HttpStatus.NOT_FOUND)
      @ExceptionHandler(value = {UnknownUserException.class, UnknownRoleException.
              class, UnknownAdvertisementException.class,
      UnknownAdvertisementException.class, UnknownCategoryException.class, UnknownImageException.class,
              UnknownUserRateException.class, UnknownMessageException.class})
      @ResponseBody
      public ErrorInfo handleNotFound(HttpServletRequest req, Exception ex) {
            return new ErrorInfo(new Timestamp(new Date().getTime()),HttpStatus.NOT_FOUND.value(),
                    HttpStatus.NOT_FOUND.name(), ex.getMessage(), req.getRequestURI());
      }
      @ResponseStatus(HttpStatus.BAD_REQUEST)
      @ExceptionHandler(value = {EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class,
              UserRateAlreadyExistsException.class,IllegalArgumentException.class, AuthException.class})
      @ResponseBody
      public ErrorInfo handleAlreadyExists(HttpServletRequest req, Exception ex) {
            return new ErrorInfo(new Timestamp(new Date().getTime()),HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.name(), ex.getMessage(), req.getRequestURI());
      }
      @ResponseStatus(HttpStatus.BAD_REQUEST)
      @ExceptionHandler(value = {ValidationException.class})
      @ResponseBody
      public ErrorInfo handleValidationException(HttpServletRequest req, ValidationException ex) {
            ValidationErrorInfo errorInfo = new ValidationErrorInfo(new Timestamp(new Date().getTime()),
                    HttpStatus.BAD_REQUEST.value(),HttpStatus.BAD_REQUEST.name(), ex.getMessage(), req.getRequestURI());
            errorInfo.setErrors(ex.getErrors());
            return errorInfo;
      }
      @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
      @ExceptionHandler(value = {DeleteMessageException.class, UpdateMessageException.class, CreateAdvertisementException.class})
      @ResponseBody
      public ErrorInfo handleDeleteUpdateMessageException(HttpServletRequest req, Exception ex) {
            return new ErrorInfo(new Timestamp(new Date().getTime()),HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    HttpStatus.UNPROCESSABLE_ENTITY.name(), ex.getMessage(), req.getRequestURI());
      }

}
