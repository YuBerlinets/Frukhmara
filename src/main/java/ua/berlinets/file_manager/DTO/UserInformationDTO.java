package ua.berlinets.file_manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.enums.RoleEnum;

import java.util.Collection;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInformationDTO {
    private String username;
    private String name;
    private String registrationDate;
    private List<Role> roles;
    private boolean accountIsConfirmed;

}
