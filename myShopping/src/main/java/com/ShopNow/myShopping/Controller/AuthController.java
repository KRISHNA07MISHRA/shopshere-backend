package com.ShopNow.myShopping.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ShopNow.myShopping.DTO.Login.LoginRequest;
import com.ShopNow.myShopping.DTO.Login.LoginResponse;
import com.ShopNow.myShopping.DTO.Register.RegisterRequest;
import com.ShopNow.myShopping.DTO.Register.RegisterResponse;
import com.ShopNow.myShopping.Service.AuthService;

import jakarta.validation.Valid;


@RestController 
@RequestMapping ("/auth") 
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid   @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping ("/login")
    public ResponseEntity<LoginResponse> login( @Valid @RequestBody LoginRequest request){
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public Map<String, String> currentUser(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "email", jwt.getSubject(),
                "name", jwt.getClaimAsString("name")
        );
    }
 
}
