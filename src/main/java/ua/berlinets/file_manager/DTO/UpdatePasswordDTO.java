package ua.berlinets.file_manager.DTO;

import lombok.Data;

@Data
public class UpdatePasswordDTO {
    private String password;
    private String password_repeat;
}

