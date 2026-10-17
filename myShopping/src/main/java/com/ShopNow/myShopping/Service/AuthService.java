package com.ShopNow.myShopping.Service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.ShopNow.myShopping.DTO.Login.LoginRequest;
import com.ShopNow.myShopping.DTO.Login.LoginResponse;
import com.ShopNow.myShopping.DTO.Register.RegisterRequest;
import com.ShopNow.myShopping.DTO.Register.RegisterResponse;
import com.ShopNow.myShopping.Entity.AppUser;

import com.ShopNow.myShopping.Entity.Role;
import com.ShopNow.myShopping.Repository.AppUserRepo;




@Service 
public class AuthService {
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    @Value ("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    public AuthService(
            AppUserRepo appUserRepo,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder) {
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        if (appUserRepo.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        AppUser user = new AppUser();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password()));

        // Role is assigned by the server, not the client.
        user.setRole(Role.USER_CUSTOMER);
        user.setActive(true);

        AppUser savedUser = appUserRepo.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    public LoginResponse login(LoginRequest request) {

    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
            )
    );

    AppUser user = appUserRepo.findByEmail(request.email())
            .orElseThrow(() ->
                    new IllegalArgumentException("Invalid email or password"));

    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("shopshere-api")
            .issuedAt(now)
            .expiresAt(now.plusMillis(jwtExpirationMs))
            .subject(user.getEmail())
            .claim("name", user.getName())
            .claim("role", user.getRole().name())
            .build();

    JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

    String token = jwtEncoder.encode(
            JwtEncoderParameters.from(header, claims)
    ).getTokenValue();

    return new LoginResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole().name(),
            token
    );
}
}
