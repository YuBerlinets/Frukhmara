package ua.berlinets.file_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.entity.User;
import ua.berlinets.file_manager.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/delete/{username}")
    public void deleteUser(@PathVariable String username) {
        userRepository.deleteByUsername(username);

    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader(name = "Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);
        if (username == null) {
            return ResponseEntity.ok(Map.of("error", "Invalid token"));
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Map<String, Object> jsonUser = new LinkedHashMap<>();
        jsonUser.put("username", user.getUsername());
        jsonUser.put("name", user.getName());
        jsonUser.put("roles", user.getRoles());
        jsonUser.put("isConfirmed", user.isAccountIsConfirmed());
        return ResponseEntity.ok(jsonUser);
    }
}
