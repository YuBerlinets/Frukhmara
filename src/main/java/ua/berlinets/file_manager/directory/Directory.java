package ua.berlinets.file_manager.directory;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Data;
import ua.berlinets.file_manager.entities.User;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Data
public class Directory {
    private User user;
    private String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");

    private List<File> files;

    public Directory(User user) {
        this.path += user.getUsername();
        this.files = List.of(Objects.requireNonNull(new File(path).listFiles()));
    }

    public List<Map<String, Object>> getInformation() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (File file : files) {
            Map<String, Object> fileInformation = new LinkedHashMap<>();
            fileInformation.put("name", file.getName());
            fileInformation.put("path", file.getPath());
            fileInformation.put("isFile", file.isFile());
            fileInformation.put("isDirectory", file.isDirectory());
            fileInformation.put("size", getFileSize(file.length()));
            DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            fileInformation.put("lastModified", sdf.format(file.lastModified()));

            result.add(fileInformation);
        }

        return result;
    }

    private String getFileSize(long fileLength) {
        String fileSize;
        if (fileLength < 1024) {
            fileSize = fileLength + " B";
        } else if (fileLength < 1024 * 1024) {
            fileSize = (fileLength / 1024) + " KB";
        } else if (fileLength < 1024 * 1024 * 1024) {
            fileSize = fileLength / (1024 * 1024) + " MB";
        } else {
            fileSize = fileLength / (1024 * 1024 * 1024) + " GB";
        }
        return fileSize;
    }

    public void createDirectoryForUser() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

}
