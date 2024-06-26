package ua.berlinets.file_manager.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.DTO.UserInformationDTO;
import ua.berlinets.file_manager.directory.DirectoryManager;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.repositories.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    public List<UserInformationDTO> getAllNotConfirmedAccounts() {
        List<User> users = userRepository.findAllByAccountIsConfirmed(false);
        List<UserInformationDTO> response = new ArrayList<>();
        for (User user : users) {
            UserInformationDTO userInfo = new UserInformationDTO(
                    user.getUsername(),
                    user.getName(),
                    user.getRegistrationDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),
                    user.getRoles(),
                    user.isAccountIsConfirmed()
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

    public User confirmUser(User user) {
        user.setAccountIsConfirmed(true);

        DirectoryManager.createDirectory(user);

        return userRepository.save(user);
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
