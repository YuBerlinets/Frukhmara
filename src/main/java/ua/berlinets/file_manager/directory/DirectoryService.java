package ua.berlinets.file_manager.directory;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.berlinets.file_manager.DTO.FileInformationDTO;
import ua.berlinets.file_manager.entities.User;
import ua.berlinets.file_manager.services.FileMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");
    private final FileMapper fileMapper;

    public List<FileInformationDTO> getInformation(User user) {
        List<FileInformationDTO> result = new ArrayList<>();
        List<File> files = List.of(Objects.requireNonNull(new File(path + user.getUsername()).listFiles()));
        for (File file : files) {
            result.add(fileMapper.fileToDTO(file));
        }
        return result;
    }

    public List<FileInformationDTO> getInformation(User user, String directory) {
        List<File> files = List.of(Objects.requireNonNull(new File(path + user.getUsername() + "/" + directory).listFiles()));
        List<FileInformationDTO> result = new ArrayList<>();
        for (File file : files) {
            result.add(fileMapper.fileToDTO(file));
        }
        return result;
    }

    public List<FileInformationDTO> getFilesByName(User user, String name) {
        List<FileInformationDTO> result = new ArrayList<>();
        String userPath = path + user.getUsername();
        Stream<Path> matches = null;
        try {
            matches = Files.find(Path.of(userPath), 10, (path, basicFileAttributes) -> path.getFileName().toString().contains(name));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        matches.forEach(f -> result.add(fileMapper.fileToDTO(new File(f.toString()))));
        matches.close();
        return result;
    }

    public boolean uploadFile(User user, MultipartFile file) {
        try {
            file.transferTo(new File(path + user.getUsername() + "/" + file.getOriginalFilename()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}