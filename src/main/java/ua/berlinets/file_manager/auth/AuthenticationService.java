package ua.berlinets.file_manager.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.directory.DirectoryManager;
import ua.berlinets.file_manager.entity.User;
import ua.berlinets.file_manager.enums.Role;
import ua.berlinets.file_manager.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return AuthenticationResponse.builder()
                    .message("User with such username already exists")
                    .build();
        }
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountIsConfirmed(false)
                .role(Role.USER)
                .roles(List.of(Role.USER))
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        if (jwtToken != null) {
            DirectoryManager.createDirectory(user);
        }
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("User has been successfully registered")
                .build();
    }

}
