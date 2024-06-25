package ua.berlinets.file_manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ua.berlinets.file_manager.enums.Role;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
public class UserInformationDTO {
    private String username;
    private String name;
    private String registrationDate;
    private Collection<Role> roles;
    private boolean accountIsConfirmed;

}
