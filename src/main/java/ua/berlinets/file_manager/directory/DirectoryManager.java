package ua.berlinets.file_manager.directory;

import io.github.cdimascio.dotenv.Dotenv;
import ua.berlinets.file_manager.entity.User;

import java.io.File;

public class DirectoryManager {

    private static final String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");

    public static void createDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void renameDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (file.exists()) {
            file.renameTo(new File(path + user.getName()));
        }
    }

    public static void deleteDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (file.exists()) {
            file.delete();
        }
    }
}
