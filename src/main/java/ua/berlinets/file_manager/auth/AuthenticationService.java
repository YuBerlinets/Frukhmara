package ua.berlinets.file_manager.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.enums.RoleEnum;
import ua.berlinets.file_manager.repositories.RoleRepository;
import ua.berlinets.file_manager.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findById(request.getUsername())
                .orElseThrow();

        if (!user.isAccountIsConfirmed())
            return AuthenticationResponse.builder()
                    .message("Account is not confirmed")
                    .build();

        var jwtToken = jwtService.generateAccessToken(user);
        var refreshToken = user.getRefreshToken();

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .message("User has been successfully authenticated")
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findById(request.getUsername()).isPresent()) {
            return AuthenticationResponse.builder()
                    .message("User with such username already exists")
                    .build();
        }
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByRoleName(RoleEnum.USER).get());

//        for (RoleEnum roleEnum : request.getRoles()) {
//            roleRepository.findByRoleName(roleEnum).ifPresent(roles::add);
//        }

        var refreshToken = jwtService.generateRefreshToken();
        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountIsConfirmed(false)
                .registrationDate(LocalDateTime.now())
                .refreshToken(refreshToken)
                .refreshTokenExp(LocalDateTime.now().plusDays(7))
                .roles(roles)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateAccessToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .message("User has been successfully registered")
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        var refreshToken = request.getRefreshToken();
        var user = userRepository.findByRefreshToken(refreshToken).orElse(null);
        if (user == null)
            return AuthenticationResponse.builder().message("No such user").build();
        if (LocalDateTime.now().isAfter(user.getRefreshTokenExp())) {
            var newAccessToken = jwtService.generateAccessToken(user);
            var newRefreshToken = jwtService.generateRefreshToken();
            user.setRefreshToken(newRefreshToken);
            user.setRefreshTokenExp(LocalDateTime.now().plusDays(7));
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .message("Token and refresh token have been successfully refreshed")
                    .build();
        }
        return AuthenticationResponse.builder()
                .token(jwtService.generateAccessToken(user))
                .refreshToken(user.getRefreshToken())
                .message("Token has been successfully refreshed").build();

    }

}
