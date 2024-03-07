package ua.berlinets.file_manager.controller;


import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.berlinets.file_manager.directory.Directory;
import ua.berlinets.file_manager.entity.User;
import ua.berlinets.file_manager.enums.Role;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/files")
public class FileController {
    private final User user = new User();

    {
        user.setName("Test User");
        user.setUsername("testUser");
        user.setPassword("123");
        user.setRoles(List.of(Role.ADMIN));
    }

    @GetMapping("/info")
    public ResponseEntity<List<Map<String, Object>>> getFilesInfo() {
        Directory directory = new Directory(user);
        return ResponseEntity.ok(directory.getInformation());
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Resource fileResource = new FileSystemResource(user.getUsername() + fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource.getFilename())
                .body(fileResource);
    }
}
