package org.example.config;


import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.example.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.Date;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

      @ResponseStatus(HttpStatus.NOT_FOUND)
      @ExceptionHandler(value = {UnknownUserException.class, UnknownRoleException.
              class,UnknownAdvertisementException.class,
      UnknownAdvertisementException.class,UnknownCategoryException.class,UnknownImageException.class,
              UnknownUserRateException.class,UnknownMessageException.class})
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
      @ExceptionHandler(value = {DeleteMessageException.class,UpdateMessageException.class,
              MaximumSavedAdsReachedException.class,CreateAdvertisementException.class,CreateMessageException.class})
      @ResponseBody
      public ErrorInfo handleDeleteUpdateMessageException(HttpServletRequest req, Exception ex) {
            return new ErrorInfo(new Timestamp(new Date().getTime()),HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    HttpStatus.UNPROCESSABLE_ENTITY.name(), ex.getMessage(), req.getRequestURI());
      }

}
