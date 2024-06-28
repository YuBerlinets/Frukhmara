package ua.berlinets.file_manager.controllers;


import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.berlinets.file_manager.directory.DirectoryService;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.UserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");
    private final DirectoryService directoryService;
    private final UserService userService;


    @GetMapping("/info")
    public ResponseEntity<Object> getFilesInfo(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(directoryService.getInformation(user));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/info-by-directory/{directory}")
    public ResponseEntity<Object> getFilesFromDirectory(@PathVariable String directory, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(directoryService.getInformation(user, directory));

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file selected");
        }
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            user = userService.getUser(userDetails.getUsername()).orElse(null);
        }
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (directoryService.uploadFile(user, file))
            return ResponseEntity.ok("File uploaded successfully");
        return ResponseEntity.badRequest().body("Error while uploading file");
    }
}
