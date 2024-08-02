package ua.berlinets.file_manager.services;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.entities.Role;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.enums.RoleEnum;
import ua.berlinets.file_manager.repositories.RoleRepository;
import ua.berlinets.file_manager.repositories.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final Dotenv dotenvBuilder = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load();
    private final String email = dotenvBuilder.get("EMAIL_USERNAME");
    private final String adminReceiverEmail = dotenvBuilder.get("EMAIL_ADMIN");
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    //TODO: update message
    @Async
    public void sendNotificationAboutNewUser(User user) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String subject = "New User Registration Notification";

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = user.getRegistrationDate().format(formatter);

            String htmlMsg = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "<style>" +
                    "    body {font-family: Arial, sans-serif; line-height: 1.6;}" +
                    "    .container {width: 80%; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}" +
                    "    h2 {color: #333;}" +
                    "    p {color: #555;}" +
                    "    .details {background-color: #fff; padding: 15px; border-radius: 8px; margin-top: 20px; border: 1px solid #ddd;}" +
                    "    .details p {margin: 5px 0;}" +
                    "</style>" +
                    "</head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<h2>New User Registered</h2>" +
                    "<p>Dear Admin,</p>" +
                    "<p>A new user has just registered on the platform. Here are the details:</p>" +
                    "<div class='details'>" +
                    "<p><strong>Username:</strong> " + user.getUsername() + "</p>" +
                    "<p><strong>Name:</strong> " + user.getName() + "</p>" +
                    "<p><strong>Registration Date:</strong> " + formattedDate + "</p>" +
                    "</div>" +
                    "<p>Please take the necessary actions.</p>" +
                    "<p>Best regards,<br>Your System</p>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            helper.setFrom(email);
            helper.setTo(adminReceiverEmail);
            helper.setSubject(subject);
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For future usage if added email to entity
    private String[] getAdminsEmail() {
        Role roleAdmin = roleRepository.findByRoleName(RoleEnum.ADMIN).orElse(null);

        if (roleAdmin == null)
            return null;

        List<User> allAdmins = userRepository.findAllByRolesContains(roleAdmin);

        if (allAdmins.isEmpty())
            return null;
        //replace username with email
        return allAdmins.stream().map(User::getUsername).toArray(String[]::new);
    }
}
