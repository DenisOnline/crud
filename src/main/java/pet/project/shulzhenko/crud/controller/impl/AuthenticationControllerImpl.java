package pet.project.shulzhenko.crud.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pet.project.shulzhenko.crud.controller.AuthenticationApi;
import pet.project.shulzhenko.crud.dto.JwtAuthenticationDto;
import pet.project.shulzhenko.crud.dto.RefreshTokenDto;
import pet.project.shulzhenko.crud.dto.UserCredentialsDto;
import pet.project.shulzhenko.crud.dto.response.UserFullDtoResponse;
import pet.project.shulzhenko.crud.dto.response.UserShortDtoResponse;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthenticationControllerImpl implements AuthenticationApi {

    private final AuthenticationService authenticationService;

    @Override
    @PostMapping("/register")
    public UserShortDtoResponse register(@Valid @RequestBody UserCredentialsDto userCredentialsDto) {
        return authenticationService.register(userCredentialsDto);
    }

    @Override
    @PostMapping("/login")
    public JwtAuthenticationDto login(@Valid @RequestBody UserCredentialsDto userCredentialsDto) {
        return authenticationService.login(userCredentialsDto);
    }

    @Override
    @GetMapping("/me")
    public UserFullDtoResponse me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return authenticationService.me(userDetails);
    }

    @Override
    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return authenticationService.refreshToken(refreshTokenDto);
    }
}