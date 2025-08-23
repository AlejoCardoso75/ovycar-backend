package com.talleres.ovycar.controller;

import com.talleres.ovycar.dto.AuthResponseDTO;
import com.talleres.ovycar.dto.LoginDTO;
import com.talleres.ovycar.dto.RegisterDTO;
import com.talleres.ovycar.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO registerDTO) {
        AuthResponseDTO response = authService.register(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthResponseDTO> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        AuthResponseDTO response = authService.validateToken(token);
        return ResponseEntity.ok(response);
    }
}
