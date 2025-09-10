package com.radiuk.auth_service.contoller;

import com.radiuk.auth_service.dto.UserAuthDto;
import com.radiuk.auth_service.dto.UserRegistrationDto;
import com.radiuk.auth_service.model.MessageResponse;
import com.radiuk.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRegistrationDto userRegistrationDto) {
        authService.register(userRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User created"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAuthDto userAuthDto) {
        return ResponseEntity.ok(Map.of("token", ""));
    }
}
