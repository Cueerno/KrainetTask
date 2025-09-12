package com.radiuk.auth_service.contoller;

import com.radiuk.auth_service.dto.UserUpdateDto;
import com.radiuk.auth_service.model.MessageResponse;
import com.radiuk.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.getMe(jwt));
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody UserUpdateDto userUpdateDto, @AuthenticationPrincipal Jwt jwt) {
        userService.updateMe(userUpdateDto, jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User updated"));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMe(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteMe(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User deleted"));
    }
}
