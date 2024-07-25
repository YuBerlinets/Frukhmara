package ua.berlinets.file_manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserInformationDTO {
    private String username;
    private String name;
    private String registrationDate;
    private String storagePlanName;
    private String usedSpace;
    private double planStorageCapacity;
    private List<String> roles;
    private boolean accountIsConfirmed;

}
