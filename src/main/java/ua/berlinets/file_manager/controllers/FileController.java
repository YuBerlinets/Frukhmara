package ua.berlinets.file_manager.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ua.berlinets.file_manager.DTO.FileInformationDTO;
import ua.berlinets.file_manager.directory.DirectoryService;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.UserService;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final DirectoryService directoryService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> getFilesInfo(@RequestParam(required = false) String path, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            try {
                if (path != null)
                    return ResponseEntity.ok(directoryService.getInformation(user, path));
                return ResponseEntity.ok(directoryService.getInformation(user));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error while getting files info: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/search/{filename}")
    public ResponseEntity<Object> searchForFile(@PathVariable String filename, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            List<FileInformationDTO> response = directoryService.getFilesByName(user, filename);
            if (!response.isEmpty())
                return ResponseEntity.ok(response);
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename, Authentication authentication) {

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Resource fileResource = directoryService.downloadFile(user, filename);

            if (fileResource == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource.getFilename())
                    .body(fileResource);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/upload-one")
    public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file, Authentication authentication) {
        if (file.isEmpty())
            return ResponseEntity.badRequest().body("No file selected");

        User user = null;

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            user = userService.getUser(userDetails.getUsername()).orElse(null);
        if (user == null)
            return ResponseEntity.notFound().build();

        if (directoryService.uploadFile(user, file))
            return ResponseEntity.ok("File uploaded successfully");

        return ResponseEntity.badRequest().body("Error while uploading file");
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFiles(@RequestParam("files") MultipartFile[] files, Authentication authentication) {
        if (files.length == 0)
            return ResponseEntity.badRequest().body("No files selected");
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails)
            user = userService.getUser(userDetails.getUsername()).orElse(null);

        if (user == null)
            return ResponseEntity.notFound().build();

        for (var file : files)
            if (!directoryService.uploadFile(user, file))
                return ResponseEntity.badRequest().body("Error while uploading file " + file.getName());

        return ResponseEntity.ok("Files uploaded successfully");
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteFile(@RequestParam String filename, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User user = userService.getUser(userDetails.getUsername()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (directoryService.deleteFile(user, filename))
                return ResponseEntity.ok("File deleted successfully");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
