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
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response;
        try {
            response = authenticationService.authenticate(request);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthenticationResponse.builder().message("Invalid username or password").build());
        }
        if (response.getToken() == null && response.getMessage().equals("Account is not confirmed")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestBody RefreshTokenRequest request) {
        var response = authenticationService.refreshToken(request);
        if (response.getRefreshToken() == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        return ResponseEntity.ok(response);
    }

}
