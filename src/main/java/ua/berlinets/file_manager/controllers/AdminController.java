package ua.berlinets.file_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.DTO.UpdateRolesDTO;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.RoleService;
import ua.berlinets.file_manager.services.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @DeleteMapping("/delete-account/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        if (userService.getUser(username).isPresent()) {
            userService.deleteUser(username);
            return ResponseEntity.ok("User " + username + " was successfully deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/confirm-account/{username}")
    public ResponseEntity<String> confirmUser(@PathVariable String username) {
        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        userService.confirmUser(user);
        return ResponseEntity.ok(user.getUsername() + " was successfully confirmed");
    }

    @GetMapping("/not-confirmed-accounts")
    public ResponseEntity<Object> getAllNotConfirmedAccounts() {
        return ResponseEntity.ok(userService.getAllNotConfirmedAccounts());
    }

    @GetMapping("/account/{username}")
    public ResponseEntity<Object> getAccount(@PathVariable String username) {
        UserInformationDTO user = userService.getUserInformation(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all-accounts")
    public ResponseEntity<Object> getAllAccounts() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/reset-password/{username}")
    public ResponseEntity<Object> resetAccountPassword(@PathVariable String username) {
        User user = userService.getUser(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        String generatedPassword = userService.resetPassword(user);
        return ResponseEntity.ok(generatedPassword);
    }

    @PutMapping("/update-roles/{username}")
    public ResponseEntity<Object> updateRoles(@PathVariable String username, @RequestBody UpdateRolesDTO roles) {
        User user = userService.getUser(username).orElse(null);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        try {
            userService.updateRoles(user, roles.getRoles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/roles")
    public ResponseEntity<Object> getAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }
}
