package ua.berlinets.file_manager.DTO;

import lombok.Data;
import ua.berlinets.file_manager.enums.RoleEnum;

import java.util.List;

@Data
public class UpdateRolesDTO {
    List<RoleEnum> roles;
}
