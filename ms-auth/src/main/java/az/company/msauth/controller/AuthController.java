package az.company.msauth.controller;

import az.company.msauth.client.UserClient;
import az.company.msauth.model.request.AuthRequest;
import az.company.msauth.model.response.AuthResponse;
import az.company.msauth.model.response.UserResponse;
import az.company.msauth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserClient userClient;

    public AuthController(JwtService jwtService, UserClient userClient) {
        this.jwtService = jwtService;
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<AuthResponse> getToken(@RequestBody AuthRequest authRequest) {
        Boolean valid = userClient.userValid("system-id", "USER", authRequest);
        if (Boolean.TRUE.equals(valid)) {
            UserResponse user = userClient.getUserByUsername("system-id", "USER", authRequest.getUsername()).getBody();
            String token = jwtService.generateToken(user.getUserId(), user.getUsername(), user.getRole());
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
