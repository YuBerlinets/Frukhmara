package ua.berlinets.file_manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.DTO.UpdatePasswordDTO;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.directory.DirectoryManager;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.enums.RoleEnum;
import ua.berlinets.file_manager.repositories.RoleRepository;
import ua.berlinets.file_manager.repositories.StoragePlanRepository;
import ua.berlinets.file_manager.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final StoragePlanRepository storagePlanRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public UserInformationDTO getUserInformation(String username) {
        return userMapper.userToDTO(userRepository.findByUsername(username).orElse(null));
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userRepository.deleteById(username);

        DirectoryManager.deleteDirectory(user);
    }

    public List<UserInformationDTO> getAllNotConfirmedAccounts() {
        List<User> users = userRepository.findAllByAccountIsConfirmed(false);
        List<UserInformationDTO> response = new ArrayList<>();
        for (User user : users) {
            response.add(userMapper.userToDTO(user));
        }
        return response;
    }

    public List<UserInformationDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserInformationDTO> userInformationDTOS = new ArrayList<>();
        for (User user : users) {
            userInformationDTOS.add(userMapper.userToDTO(user));
        }
        return userInformationDTOS;
    }

    public User confirmUser(User user) {
        user.setAccountIsConfirmed(true);

        DirectoryManager.createDirectory(user);

        return userRepository.save(user);
    }

    public UserInformationDTO getUserInformation(User user) {
        return userMapper.userToDTO(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>()
        );
    }

    public void updatePassword(String username, UpdatePasswordDTO request) {
        User user = userRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public String resetPassword(User user) {
        String newPassword = PasswordGenerator.generatePassword();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return newPassword;
    }

    public void updateRoles(User user, List<RoleEnum> roles) {
        List<Role> foundRoles = new ArrayList<>();
        for (RoleEnum role : roles) {
            Role foundRole = roleRepository.findByRoleName(role)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            foundRoles.add(foundRole);
        }

        user.setRoles(foundRoles);
        userRepository.save(user);
    }
}
