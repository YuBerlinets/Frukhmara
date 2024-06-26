package ua.berlinets.file_manager.directory;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import ua.berlinets.file_manager.entities.User;

import java.io.File;

@Service
public class DirectoryManager {

    private static final String path = Dotenv.configure()
            .directory("src/main/resources")
            .filename(".env")
            .load().get("FILE_STORAGE_PATH");

    public static boolean createDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (!file.exists())
            return file.mkdirs();
        return false;
    }

    public static boolean renameDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (file.exists())
            return file.renameTo(new File(path + user.getName()));

        return false;
    }

    public static boolean deleteDirectory(User user) {
        File file = new File(path + user.getUsername());
        if (file.exists())
            return file.delete();
        return false;
    }
}
