package ua.berlinets.file_manager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.repositories.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    public List<Map<String, Object>> getAllNotConfirmedAccounts() {
        List<User> users = userRepository.findAllByAccountIsConfirmed(false);
        List<Map<String, Object>> response = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userInfo = Map.of(
                    "username", user.getUsername(),
                    "name", user.getName(),
                    "isConfirmed", user.isAccountIsConfirmed(),
                    "registrationDate", user.getRegistrationDate(),
                    "roles", user.getRoles()
            );
            response.add(userInfo);
        }
        return response;
    }

    public List<UserInformationDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserInformationDTO> userInformationDTOS = new ArrayList<>();
        for (User user : users) {

            userInformationDTOS.add(
                    new UserInformationDTO(
                            user.getUsername(),
                            user.getName(),
                            user.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                            user.getRoles(),
                            user.isAccountIsConfirmed()));
        }
        return userInformationDTOS;
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
}
