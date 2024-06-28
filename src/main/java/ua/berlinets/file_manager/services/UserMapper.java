package ua.berlinets.file_manager.services;

import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.entities.User;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserMapper {
    public UserInformationDTO userToDTO(User user) {
        List<String> roles = new ArrayList<>();
        user.getRoles().forEach(role -> roles.add(role.getRoleName().name()));
        return UserInformationDTO.builder()
                .username(user.getUsername())
                .name(user.getName())
                .registrationDate(user.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .roles(roles)
                .accountIsConfirmed(user.isAccountIsConfirmed())
                .build();
    }
}
