package ua.berlinets.file_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.entity.User;
import ua.berlinets.file_manager.repository.UserRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            userRepository.deleteByUsername(username);
            return ResponseEntity.status(HttpStatus.OK).body("User " + username + " was successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/confirm_account/{username}")
    public ResponseEntity<String> confirmUser(@PathVariable String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setAccountIsConfirmed(true);
        userRepository.save(user);
        return ResponseEntity.ok(user.getUsername() + " was successfully confirmed");
    }

    @GetMapping("/not_confirmed_accounts")
    public ResponseEntity<List<Map<String, Object>>> getAllNotConfirmedAccounts() {
        List<User> users = userRepository.findAllByAccountIsConfirmed(false);
        if (!users.isEmpty()) {
            List<Map<String, Object>> response = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userInfo = new LinkedHashMap<>();
                userInfo.put("username", user.getUsername());
                userInfo.put("name", user.getName());
                userInfo.put("isConfirmed", user.isAccountIsConfirmed());
                response.add(userInfo);
            }
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(new ArrayList<>());
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
