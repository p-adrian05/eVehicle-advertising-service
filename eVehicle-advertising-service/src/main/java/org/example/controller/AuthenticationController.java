package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.Mappings;
import org.example.controller.dto.user.AuthenticationRequestDto;
import org.example.controller.dto.user.AuthenticationResponseDto;
import org.example.core.security.AuthException;
import org.example.core.user.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final LoginService loginService;

    @PostMapping(Mappings.AUTHENTICATE)
    @CrossOrigin
    public AuthenticationResponseDto authenticate(@RequestBody AuthenticationRequestDto authenticationRequestDto)
        throws AuthException, javax.security.auth.message.AuthException {

        return new AuthenticationResponseDto(
            loginService.login(authenticationRequestDto.getUsername(),authenticationRequestDto.getPassword()));
    }

    @GetMapping(Mappings.AUTHENTICATE_ACTIVATE+"/{code}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String registrationActivation(@PathVariable String code)
        throws AuthException, javax.security.auth.message.AuthException {
        loginService.activateUser(code);
        return "Successful activation";
    }
}
