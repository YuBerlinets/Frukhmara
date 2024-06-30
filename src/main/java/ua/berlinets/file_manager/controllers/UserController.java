package ua.berlinets.file_manager.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.DTO.UpdatePasswordDTO;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping("/my-information")
    public ResponseEntity<Object> getUserInfo(@RequestHeader(name = "Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtService.extractUsername(jwtToken);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        User user = userService.getUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserInformationDTO userInformationDTO = userService.getUserInformation(user);

        return ResponseEntity.ok(userInformationDTO);
    }

    @PostMapping("/update-password")
    public ResponseEntity<Object> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!updatePasswordDTO.getPassword().equals(updatePasswordDTO.getPassword_repeat())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }
        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails userDetails)
            username = userDetails.getUsername();

        userService.updatePassword(username, updatePasswordDTO);
        return ResponseEntity.ok("Password updated successfully");
    }

}
