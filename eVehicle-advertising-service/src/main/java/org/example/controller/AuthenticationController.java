package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.Mappings;
import org.example.controller.dto.user.AuthenticationRequestDto;
import org.example.controller.dto.user.AuthenticationResponseDto;
import org.example.security.exception.AuthException;
import org.example.core.user.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthenticationController {

    private final LoginService loginService;

    @PostMapping(Mappings.AUTHENTICATE)
    @CrossOrigin
    @ResponseBody
    public AuthenticationResponseDto authentication(@RequestBody AuthenticationRequestDto authenticationRequestDto)
        throws AuthException{
        String token =
            loginService.login(authenticationRequestDto.getUsername(),authenticationRequestDto.getPassword());
        return new AuthenticationResponseDto(token);
    }

    @GetMapping(Mappings.AUTHENTICATE_ACTIVATE+"/{code}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public String registrationActivation(@PathVariable String code)
        throws AuthException {
        loginService.activateUser(code);
        return "Successful activation";
    }
}
