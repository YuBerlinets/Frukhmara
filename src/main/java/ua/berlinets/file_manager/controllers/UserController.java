package ua.berlinets.file_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.UserService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/admin/delete/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        if (userService.getUser(username).isPresent()) {
            userService.deleteUser(username);
            return ResponseEntity.status(HttpStatus.OK).body("User " + username + " was successfully deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/admin/confirm_account/{username}")
    public ResponseEntity<String> confirmUser(@PathVariable String username) {
        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        user.setAccountIsConfirmed(true);
        userService.saveUser(user);
        return ResponseEntity.ok(user.getUsername() + " was successfully confirmed");
    }

    @GetMapping("/admin/not_confirmed_accounts")
    public ResponseEntity<List<Map<String, Object>>> getAllNotConfirmedAccounts() {
        List<Map<String, Object>> response = userService.getAllNotConfirmedAccounts();
        if (!response.isEmpty()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/admin/all_accounts")
    public ResponseEntity<List<UserInformationDTO>> getAllAccounts() {
        List<UserInformationDTO> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
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
        User user = userService.getUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        Map<String, Object> jsonUser = new LinkedHashMap<>();
        jsonUser.put("username", user.getUsername());
        jsonUser.put("name", user.getName());
        jsonUser.put("registrationDate", user.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        jsonUser.put("roles", user.getRoles());
        jsonUser.put("isConfirmed", user.isAccountIsConfirmed());
        return ResponseEntity.ok(jsonUser);
    }
}
