package ua.berlinets.file_manager.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response;
        try {
            response = authenticationService.authenticate(request);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("Invalid username or password").build());
        }
        if (response.getToken() == null && response.getMessage().equals("Account is not confirmed")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

}
