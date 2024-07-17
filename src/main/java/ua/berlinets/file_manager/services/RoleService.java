package ua.berlinets.file_manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.enums.RoleEnum;
import ua.berlinets.file_manager.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public List<RoleEnum> findAll() {
        List<Role> roles = roleRepository.findAll();
        List<RoleEnum> result = new ArrayList<>();
        for (Role role : roles) {
            result.add(role.getRoleName());
        }
        return result;
    }
}
