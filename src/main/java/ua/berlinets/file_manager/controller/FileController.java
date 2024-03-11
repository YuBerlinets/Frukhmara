package ua.berlinets.file_manager.controller;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.config.JwtService;
import ua.berlinets.file_manager.directory.Directory;
import ua.berlinets.file_manager.entity.User;
import ua.berlinets.file_manager.repository.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final UserRepository userRepository;
    private final String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");


    @GetMapping("/info")
    public ResponseEntity<List<Map<String, Object>>> getFilesInfo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            Directory directory = new Directory(user);
            return ResponseEntity.ok(directory.getInformation());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Resource fileResource = new FileSystemResource(path + "/" + username + "/" + fileName);

            if (!fileResource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource.getFilename())
                    .body(fileResource);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
